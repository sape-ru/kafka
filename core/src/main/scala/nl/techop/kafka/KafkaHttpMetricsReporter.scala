package nl.techop.kafka

import java.net.InetSocketAddress
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.yammer.metrics.reporting.{MetricsServlet, PingServlet, ThreadDumpServlet}
import kafka.metrics.{KafkaMetricsConfig, KafkaMetricsReporterMBean, KafkaServerMetricsReporter}
import kafka.server.KafkaServer
import kafka.utils.{Logging, VerifiableProperties}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler, ServletHolder}
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer

private trait KafkaHttpMetricsReporterMBean extends KafkaMetricsReporterMBean

private class KafkaHttpMetricsReporter extends KafkaServerMetricsReporter
                              with KafkaHttpMetricsReporterMBean
                              with Logging {
  val defaultPort = 8080
  val defaultBindAddress = "localhost"

  private var metricsServer: Server = null

  private var initialized = false
  private var configured = false
  private var running = false

  private var metricsConfig: KafkaMetricsConfig = null
  private var bindAddress: String = null
  private var port: Int = 0

  override def getMBeanName = "kafka:type=kafka.metrics.KafkaHttpMetricsReporter"

  override def init(props: VerifiableProperties): Unit = {
    if (!initialized) {
      metricsConfig = new KafkaMetricsConfig(props)

      bindAddress = props.getString("kafka.http.metrics.host", defaultBindAddress)
      port = props.getInt("kafka.http.metrics.port", defaultPort)

      if (props.getBoolean("kafka.http.metrics.reporter.enabled", default = false)) {
        initialized = true
        startReporter(metricsConfig.pollingIntervalSecs)
      }
    }
  }

  override def setServer(server: KafkaServer) {
    synchronized {
      if (initialized && !configured) {
        // creating the socket address for binding to the specified address and port
        val inetSocketAddress = new InetSocketAddress(bindAddress, port)

        // create new Jetty server
        metricsServer = new Server(inetSocketAddress)

        // creating the servlet context handler
        val handler = new ServletContextHandler(metricsServer, "/*")

        // Add a default 404 Servlet
        addMetricsServlet(handler, new DefaultServlet() with NoDoTrace, "/")

        // Add Metrics Servlets
        addMetricsServlet(handler, new MetricsServlet() with NoDoTrace, "/api/metrics")
        addMetricsServlet(handler, new ThreadDumpServlet() with NoDoTrace, "/api/threads")
        addMetricsServlet(handler, new PingServlet() with NoDoTrace, "/api/ping")

        // Add Custom Servlets
        val resourceConfig: ResourceConfig = new ResourceConfig
        resourceConfig.register(new JacksonJsonProvider(), 0)
        resourceConfig.register(new KafkaTopicsResource(server), 0)

        val servletContainer: ServletContainer = new ServletContainer(resourceConfig)
        val servletHolder: ServletHolder = new ServletHolder(servletContainer)
        handler.addServlet(servletHolder, "/api/*")

        // Add the handler to the server
        metricsServer.setHandler(handler)

        configured = true
        startReporter(metricsConfig.pollingIntervalSecs)
      } else {
        error("Kafka Http Metrics Reporter already initialized")
      }
    }
  }

  private def addMetricsServlet(context: ServletContextHandler, servlet: HttpServlet, urlPattern: String): Unit = {
    context.addServlet(new ServletHolder(servlet), urlPattern)
  }

  private trait NoDoTrace extends HttpServlet {
    override def doTrace(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
      resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
    }
  }

  override def startReporter(pollingPeriodSecs: Long) {
    synchronized {
      if (initialized && configured && !running) {
        metricsServer.start()
        running = true
        info(s"Started Kafka HTTP metrics reporter at ${metricsServer.getURI}")
      } else if (running) {
        error("Kafka Http Metrics Reporter already running")
      }
    }
  }

  override def stopReporter() {
    synchronized {
      if (initialized && configured && running) {
        metricsServer.stop()
        running = false
        info("Stopped Kafka CSV metrics reporter")
      } else if (!running) {
        error("Kafka Http Metrics Reporter already stopped")
      }
    }
  }
}
