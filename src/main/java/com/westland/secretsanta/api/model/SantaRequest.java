package com.westland.secretsanta.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.westland.secretsanta.client.model.Participant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize
public class SantaRequest {

    private List<Participant> participants;
    private Map<String, String> exclusions;

    public SantaRequest(List<Participant> participants, Map<String, String> exclusions ) {
        this.participants = participants;
        this.exclusions = exclusions;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Map<String, String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(HashMap<String, String> exclusions) {
        this.exclusions = exclusions;
    }

}
