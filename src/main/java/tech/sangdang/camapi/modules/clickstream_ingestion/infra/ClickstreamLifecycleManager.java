package tech.sangdang.camapi.modules.clickstream_ingestion.infra;

import com.azure.messaging.eventhubs.EventProcessorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClickstreamLifecycleManager implements SmartLifecycle {
    private final EventProcessorClient eventProcessorClient;
    private volatile boolean running = false;

    @Override
    public void start() {
        if (!running) {
            log.info("Starting ClickstreamListener...");
            eventProcessorClient.start();
            running = true;
        }
    }

    @Override
    public void stop() {
        if (running) {
            log.info("Stopping ClickstreamListener...");
            eventProcessorClient.stop();
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
