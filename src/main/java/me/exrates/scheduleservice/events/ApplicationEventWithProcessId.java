package me.exrates.scheduleservice.events;

import org.springframework.context.ApplicationEvent;

import java.util.Optional;

import static me.exrates.ProcessIDManager.getProcessIdFromCurrentThread;


public class ApplicationEventWithProcessId extends ApplicationEvent {


    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ApplicationEventWithProcessId(Object source) {
        super(source);
        processId = getProcessIdFromCurrentThread();
    }

    public Optional<String> getProcessId() {
        return processId;
    }

    private Optional<String> processId;

}
