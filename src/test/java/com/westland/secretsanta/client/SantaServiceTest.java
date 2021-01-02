package com.westland.secretsanta.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westland.secretsanta.api.model.SantaRequest;
import com.westland.secretsanta.client.model.Participant;
import com.westland.secretsanta.client.model.SantaMatches;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SantaServiceTest {
    EmailServiceImpl impl = Mockito.mock(EmailServiceImpl.class);
    SantaService service = new SantaService(impl);

    @Test
    public void serviceNotNull() {
        assertNotNull(service);
    }

    @Test
    public void everybodyMatched() {
        List<Participant> participantList = getParticipants();
        Map<String, String> exclusions = new HashMap<>();
        exclusions.put("email1@email.com", "email2@email.com");
        SantaRequest request = new SantaRequest(participantList, exclusions);
        SantaMatches matches = this.service.generateMatches(request);

        Participant matchedToPerson1 = matches.getMatches().get("email1@email.com");
        assertEquals("email3@email.com", matchedToPerson1.getEmail());
        assertEquals(3, matches.getMatches().size());
    }

    @Test
    public void noSelfMatches() {
        List<Participant> participantList = getParticipants();
        SantaRequest request = new SantaRequest(participantList, null);
        SantaMatches matches = this.service.generateMatches(request);

        matches.getMatches().entrySet().forEach(m -> assertNotEquals(m.getKey(), m.getValue().getEmail()));

    }

    @Test
    public void noReflectiveMatches() {
        List<Participant> participantList = getParticipants();
        SantaRequest request = new SantaRequest(participantList, null);
        SantaMatches matches = this.service.generateMatches(request);

        Map<String, String> simplifiedMatches = new HashMap<>();
        matches.getMatches().entrySet().stream().forEach(e ->
                simplifiedMatches.put(e.getKey(), e.getValue().getEmail()));

        simplifiedMatches.entrySet().forEach(e -> {
            String matchee = e.getValue();
            String matcheesMatch = simplifiedMatches.get(matchee);
            assertNotEquals(matcheesMatch, e.getKey());
        });
    }

    @Test
    public void worksWithEvenNumbers() {
        List<Participant> participantList = getParticipants();
        participantList.add(new Participant("email4@email.com", "Person4", "Address"));
        SantaRequest request = new SantaRequest(participantList, null);
        SantaMatches matches = this.service.generateMatches(request);

        Map<String, String> simplifiedMatches = new HashMap<>();
        matches.getMatches().entrySet().stream().forEach(e ->
                simplifiedMatches.put(e.getKey(), e.getValue().getEmail()));

        simplifiedMatches.entrySet().forEach(e -> {
            // assert no reflective match
            String matchee = e.getValue();
            String matcheesMatch = simplifiedMatches.get(matchee);
            assertNotEquals(matcheesMatch, e.getKey());
            // assert no self match
            assertNotEquals(e.getKey(), e.getValue());
        });
    }

    @Test
    public void exclusionsTakenIntoAccount() throws JsonProcessingException {
        Map<String, String> exclusions = new HashMap<>();
        exclusions.put("email1@email.com", "email2@email.com");
        SantaRequest request = new SantaRequest(getParticipants(), exclusions);

        SantaMatches matches = service.generateMatches(request);

        var ex = request.getExclusions();
        matches.getMatches().entrySet().stream().forEach(e -> {
                    if (ex.containsKey(e.getKey())) {
                        Assert.assertNotEquals(ex.get(e.getKey()), e.getValue().getEmail());
                    }
                    if (ex.containsKey(e.getValue())) {
                        var entry = ex.entrySet().stream().filter(f -> f.getKey().equalsIgnoreCase(e.getValue().getEmail()))
                                .findAny();
                        if (entry.isPresent()) {
                            Assert.assertNotEquals(entry.get().getKey(), e.getKey());
                        }
                    }
                }
        );

    }

    @Test
    public void splitExclusions() {
        List<Participant> participantList = getParticipants();
        participantList.add(new Participant("email4@email.com", "Person4", "Address"));
        Map<String, String> exclusions = new HashMap<>();
        exclusions.put("email1@email.com", "email2@email.com");
        exclusions.put("email3@email.com", "email4@email.com");
        SantaRequest request = new SantaRequest(participantList, exclusions);
        SantaMatches matches = this.service.generateMatches(request);

        Participant matchedToPerson1 = matches.getMatches().get("email1@email.com");
        Participant matchedToPerson2 = matches.getMatches().get("email2@email.com");
        assertEquals("email3@email.com", matchedToPerson1.getEmail());
        assertEquals("email4@email.com", matchedToPerson2.getEmail());
        assertEquals(4, matches.getMatches().size());
    }

    private List<Participant> getParticipants() {
        List<Participant> participantList = new ArrayList<>();
        participantList.add(new Participant("email1@email.com", "Person1", "Address"));
        participantList.add(new Participant("email2@email.com", "Person2", "Address"));
        participantList.add(new Participant("email3@email.com", "Person3", "Address"));
        return participantList;
    }

}
