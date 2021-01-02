package com.westland.secretsanta.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westland.secretsanta.api.model.SantaRequest;
import com.westland.secretsanta.api.model.SantaResponse;
import com.westland.secretsanta.client.model.Participant;
import com.westland.secretsanta.client.model.SantaMatches;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SantaService {
    private EmailServiceImpl emailService;

    public SantaService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    public ResponseEntity<SantaResponse> assignSantas(SantaRequest request) throws JsonProcessingException {
        SantaResponse response = new SantaResponse();
        System.out.println("THIS MANY EXCLUSIONS "
                + request.getExclusions().size());
        try {
            Assert.notNull(request, "Request is null");
            SantaMatches matches = this.generateMatches(request);

            // validate matches
            this.validateMatches(request, matches);

            // Send email if validation succeeds.
            matches.getMatches().entrySet().forEach(m ->
            {
                Participant participant = m.getValue();
                this.emailService
                        .sendSimpleMessage(m.getKey()
                                , "Secret Santa 2020!"
                                , getMessageBody(participant));
            });

        } catch (IllegalArgumentException arg) {
            response.setStatus(arg.getMessage());
            ResponseEntity<SantaResponse> respEntity = new ResponseEntity<SantaResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            return respEntity;
        }
        response.setStatus("SUCCESS");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void validateMatches(SantaRequest request, SantaMatches matches) {
        // Assert everybody is matched
        Assert.isTrue(request.getParticipants().size() == matches.getMatches().size()
                , "Not everybody who is participating was matched!");
        // Assert no self matches
        Map<String, String> simplifiedMatches = new HashMap<>();
        matches.getMatches().entrySet().stream().forEach(e ->
                simplifiedMatches.put(e.getKey(), e.getValue().getEmail()));

        simplifiedMatches.entrySet().forEach(e -> {
            // assert no reflective match
            String matchee = e.getValue();
            String matcheesMatch = simplifiedMatches.get(matchee);
            Assert.isTrue(!matcheesMatch.equalsIgnoreCase(e.getKey()), "Two people were matched to each other.");
            // assert no self match
            Assert.isTrue(!e.getKey().equalsIgnoreCase(e.getValue()), "Someone was matched to themselves");
        });

        // Assert no reflective matches
        var ex = request.getExclusions();
        matches.getMatches().entrySet().stream().forEach(e -> {
                    if (ex.containsKey(e.getKey())) {
                        Assert.isTrue(!ex.get(e.getKey()).equalsIgnoreCase(e.getValue().getEmail()), "Someone was matched to a person defined in the exclusions");
                    }
                    if (ex.containsKey(e.getValue())) {
                        var entry = ex.entrySet().stream().filter(f -> f.getKey().equalsIgnoreCase(e.getValue().getEmail()))
                                .findAny();
                        if (entry.isPresent()) {
                            Assert.isTrue(!entry.get().getKey().equalsIgnoreCase(e.getKey()), "Someone was matched to a person defined in the exclusions");
                        }
                    }
                }
        );
    }

    private String getMessageBody(Participant participant) {
        return "Dear friend,\n\n You have been matched to " + participant.getName() + " for secret santa!\n" +
                " Please make sure to get a gift with a limit of $30 for your shit friend.\n" +
                " Mail your gift to " + participant.getName() + " by 12/20!! Their address is below:\n" +
                participant.getAddress() + "\n\n" +
                "You should get a gift in the mail too.\n\n" +
                "" +
                "Love ,\n" +
                "The Elves" +
                "\n\n" ;
    }

    public SantaMatches generateMatches(SantaRequest request) {

        int i = 0;
        List<Participant> participants = request.getParticipants();
        Map<String, String> exclusions = request.getExclusions();

        Map<String, Participant> matchList = new HashMap<>();

        // make sure exclusions are not first+last in the same list, in case they get matched
        // on an odd list
        List<SortableItem> sortablePs = participants.stream()
                .map(p -> new SortableItem(p)).collect(Collectors.toList());

        if (null != exclusions) {
//            HashMap<String, String> halfhmap1 = new HashMap<>();
//            HashMap<String, String> halfhmap2 = new HashMap<>();
//
//            int count = 0;
//            for (Map.Entry<String, String> entry : exclusions.entrySet()) {
//                (count < (exclusions.size() / 2) ? halfhmap1 : halfhmap2).put(entry.getKey(), entry.getValue());
//                count++;
//            }

            exclusions.entrySet().forEach(e -> {
                sortablePs.stream()
                        .filter(p -> p.getEmail().equalsIgnoreCase(e.getKey())).forEach(
                        m -> m.setSortOrder(0));
                sortablePs.stream()
                        .filter(p -> p.getEmail().equalsIgnoreCase(e.getValue()))
                        .forEach(p -> p.setSortOrder(0));
            });

//            halfhmap2.entrySet().forEach(e -> {
//                sortablePs.stream()
//                        .filter(p -> p.getEmail().equalsIgnoreCase(e.getKey())).forEach(
//                                m -> m.setSortOrder(1));
//                sortablePs.stream()
//                        .filter(p -> p.getEmail().equalsIgnoreCase(e.getValue()))
//                        .forEach(p -> p.setSortOrder(1));
//            });

        }

        // split into two lists
        int total = participants.size();
        int list1SplitPoint = total % 2 == 0 ? total / 2 : total - total / 2;

        List<SortableItem> sortedItems = sortablePs.stream().sorted(Comparator.comparing(SortableItem::getSortOrder)).collect(Collectors.toList());

        List<SortableItem> list1 = sortedItems.subList(0, list1SplitPoint);
        List<SortableItem> list2 = sortedItems.subList(list1SplitPoint, total);
        list1.stream().forEach(l1 -> {
            SortableItem matched = null;
            int curIndex = list1.indexOf(l1);

            // this does not always work because list1 is bigger than list2
            if (curIndex > list2.size() - 1) {
                matched = list1.get(0);
            } else {
                matched = list2.get(curIndex);
            }

            Participant matchInfo = new Participant(matched.getEmail(), matched.getName(), matched.getAddress());
            matchList.put(l1.getEmail(), matchInfo);
        });

        list2.stream().forEach(l2 -> {
            SortableItem matched = null;
            int curIndex = list2.indexOf(l2);

            // this does not always work because list1 is bigger than list2
            if (curIndex + 1 > list1.size() - 1) {
                matched = list1.get(0);
            } else {
                matched = list1.get(curIndex + 1);
            }

            Participant matchInfo = new Participant(matched.getEmail(), matched.getName(), matched.getAddress());
            matchList.put(l2.getEmail(), matchInfo);
        });

        SantaMatches matches = new SantaMatches();
        matches.setMatches(matchList);

        Assert.isTrue(!matches.getMatches().entrySet().stream().anyMatch(e -> e.getKey() == e.getValue().getEmail())
                , "Someone was matched to themselves! Please try again with different JSON order.");

        return matches;
    }

    class SortableItem extends Participant {

        private int sortOrder = 1;

        public SortableItem(Participant p) {
            super(p.getEmail(), p.getName(), p.getAddress());
        }

        public int getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
        }

    }
}
