package com.b1ngoos.tutor.agent;

import com.b1ngoos.tutor.models.*;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TutorAgent extends Agent {

    enum Step {
        REQUEST_TITLE_RESPONSE,
        REQUEST_CHAPTER_RESPONSE,
        DONE
    }

    CollectionCourse courses = null;
    CollectionChapter chapters = null;

    class PickUpBehaviour extends Behaviour {

        private Step step = Step.REQUEST_TITLE_RESPONSE;
        Set<Integer> coursesId = new HashSet<>();

        @Override
        public void action() {
            System.out.println("\t\tPickUpBehaviour");
            switch (getStep()) {
                case REQUEST_TITLE_RESPONSE: {
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                    ACLMessage msg = receive(mt);
                    if (msg != null) {
                        String content = msg.getContent().trim().toLowerCase();
                        System.out.println("\tCHECK TITLE COURSE " + getName() + " - " + content);
                        ACLMessage reply = msg.createReply();
                        boolean inStock = false;

                        for (Course course : courses.getCourseList()) {
                            if (course.getTitle().trim().toLowerCase().contains(content)) {
                                if (!inStock)
                                    inStock = true;
                                coursesId.add(course.getCourseId());
                            }
                        }

                        if (inStock) {
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent("inStock");
                        } else {
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("not-available");
                            setStep(Step.DONE);
                        }
                        send(reply);
                        setStep(Step.REQUEST_CHAPTER_RESPONSE);
                    } else {
                        block();
                    }
                    break;
                }
                case REQUEST_CHAPTER_RESPONSE: {

                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    ACLMessage msg = receive(mt);
                    if (msg != null) {
                        String[] requireChapters = null;
                        try {
                            requireChapters = (String[]) msg.getContentObject();
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }

                        //String content = msg.getContent();

                        List<String> chapterReply = new ArrayList<>();

                        for (String requireChapter : requireChapters) {
                            for (Chapter chapter : chapters.getChapterList(-1)) {
                                if (coursesId.contains(chapter.getCourseId())) {
                                    if (chapter.getTitle().trim().toLowerCase().
                                            contains(requireChapter.trim().toLowerCase())) {
                                        chapterReply.add(convertToReply(chapter));
                                    }
                                }
                            }
                        }

                        ACLMessage reply = msg.createReply();
                        System.out.println("\tCHECK CHAPTERS " + getName() + " - " + chapterReply.size());

                        chapterReply.forEach(System.out::println);

                        if (chapterReply.size() > 0) {
                            reply.setPerformative(ACLMessage.INFORM);
                            try {
                                reply.setContentObject((Serializable) chapterReply);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            reply.setPerformative(ACLMessage.FAILURE);
                            reply.setContent("not-available");
                        }

                        send(reply);
                        setStep(Step.DONE);
                    } else {
                        block();
                    }
                    break;
                }
            }
        }

        private String convertToReply(Chapter chapter) {

            return chapter.getTitle() + ":" +
                    chapter.getDuration() + ":" +
                    chapter.getPrice() + ":" +
                    chapter.getComplexity() + ":" +
                    String.valueOf(chapter.getCourseId()) + ":" +
                    getLocalName();
        }

        @Override
        public boolean done() {
            if (getStep() == Step.DONE)
                System.out.println("\t" + getName() + " done");
            return getStep() == Step.DONE;
        }

        public Step getStep() {
            return step;
        }

        public void setStep(Step step) {
            this.step = step;
        }
    }

    class CoursesBehaviour extends Behaviour {

        public boolean done = false;

        @Override
        public void action() {
            System.out.println("\t\tCoursesBehaviour");
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                List<String> re = new ArrayList<>();
                re.add("Agent name: " + myAgent.getLocalName());

                for (Course course : courses.getCourseList()) {
                    re.add("\tCourse: " + course.getTitle());
                    for (Chapter chapter : chapters.getChapterList(course.getCourseId())) {
                        re.add("\t\tChapter: " + chapter.getTitle() +
                                " \n\t\t\tComplexity: " + chapter.getComplexity() +
                                " \n\t\t\tDuration: " + chapter.getDuration() +
                                " \n\t\t\tPrice: " + chapter.getPrice());
                    }
                }

                try {
                    reply.setContentObject((Serializable) re);
                    send(reply);
                    System.out.println("SendReply");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                done = true;
            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return done;
        }
    }

    public void setup() {

        courses = CollectionCourse.getInstance();
        chapters = CollectionChapter.getInstance();

        System.out.println("Hello. My name is " + getLocalName());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("Tutor");
        sd.setName("TutorServiceDescription");
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);

        } catch (FIPAException ex) {
            Logger.getLogger(TutorAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Registered with the DF : " + getLocalName());

        addBehaviour(new PickUpBehaviour());
        addBehaviour(new CoursesBehaviour());
    }
}
