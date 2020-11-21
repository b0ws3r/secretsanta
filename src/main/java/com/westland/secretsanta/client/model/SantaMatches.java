package com.westland.secretsanta.client.model;

import java.util.Map;

public class SantaMatches {
    private Map<String, Participant> matches;

    public SantaMatches(){
        super();
    }

    public Map<String, Participant> getMatches() {
        return matches;
    }

    public void setMatches(Map<String, Participant> matches) {
        this.matches = matches;
    }


}
