package com.westland.secretsanta.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westland.secretsanta.api.model.SantaRequest;
import com.westland.secretsanta.client.model.Participant;
import com.westland.secretsanta.client.model.SantaMatches;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SantaService {
    private EmailServiceImpl emailService;

    public SantaService(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    public String assignSantas(SantaRequest request) throws JsonProcessingException {
        Assert.notNull(request, "Request is null");
        SantaMatches matches = this.generateMatches(request);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(matches));
        try {
            matches.getMatches().entrySet().forEach(m ->
            {
                Participant pooFren = m.getValue();
                this.emailService
                        .sendSimpleMessage(m.getKey()
                                , "**TEST** Secret Santa 2020 BITCH!!!"
                                , getMessageBody(pooFren));
            });
        }
        catch (Exception e){
            System.out.println("Something terrible happened");
        }

        return null;
    }

    private String getMessageBody(Participant pooFren) {
        return "***THIS IS A TEST - do not send a gift***\n\nYou have been matched to " + pooFren.getName() + " for secret santa!\n" +
                " Please make sure to get a gift with a limit of $40 for your shit friend.\n" +
                " Mail your gift to " + pooFren.getName() + "'s habitat by 12/20!!\n" +
                pooFren.getAddress() + "\n\n" +
                "You should get a gift in the mail too, unless I fucked up this app OR unless your secret santa sucks and forgot about you.\n\n" +
                "" +
                "Love y'all,\n" +
                "Missy" +
                "\n\n" +
                "P.S., if you want to be extra secret, you can put a common return address of 105 West Branch Lane, Stowe, VT, 05672 on your package. This way, your Santa won't even know who got them." +
                " If, however, you got someone who lives at 105 West Branch lane, you're on your own.";
    }

    public SantaMatches generateMatches(SantaRequest request) {

        int i = 0;
        List<Participant> participants = request.getParticipants();
        Map<String, String> exclusions = request.getExclusions();

        Map<String, Participant> matchList = new HashMap<>();

        // make sure exclusions are not first+last in the same list, in case they get matched
        // on an odd list
        List<SortableItem> sortablePs = participants.stream().map(p -> new SortableItem(p)).collect(Collectors.toList());
        if (null != exclusions) {
            exclusions.entrySet().forEach(e -> {
                SortableItem itemInList = sortablePs.stream().filter(p -> p.getEmail().equalsIgnoreCase(e.getKey())).findAny().orElse(null);
                SortableItem prohibitedMatch = sortablePs.stream().filter(p -> p.getEmail().equalsIgnoreCase(e.getKey())).findAny().orElse(null);
                itemInList.setSortOrder(0);
                prohibitedMatch.setSortOrder(0);
            });
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

        public SortableItem(Participant p) {
            super(p.getEmail(), p.getName(), p.getAddress());
        }

        private int sortOrder;

        public int getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
        }

    }
}
