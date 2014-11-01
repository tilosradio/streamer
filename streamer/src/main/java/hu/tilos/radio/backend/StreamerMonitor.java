package hu.tilos.radio.backend;

import org.apache.deltaspike.core.api.jmx.JmxManaged;
import org.apache.deltaspike.core.api.jmx.MBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

@MBean(description = "StreamMonitor", name = "StreamerMonitor", category = "hu.tilos.radio.stream")
@Singleton
public class StreamerMonitor implements StreamerMonitorMBean {

    private static final Logger LOG = LoggerFactory.getLogger(StreamerMonitor.class);

    @JmxManaged(description = "description currently active archive listeners")
    private AtomicInteger counter = new AtomicInteger();

    public int getCounter() {
        return counter.get();
    }

    public void decrement() {
        this.counter.decrementAndGet();
    }

    public void increment() {

        this.counter.incrementAndGet();
    }
}
