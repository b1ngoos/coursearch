package com.b1ngoos.student.agent;

import com.b1ngoos.student.models.Chapter;
import com.b1ngoos.student.start.Main;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import jade.wrapper.StaleProxyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class StudentAgent extends SearchAgent {

    enum Step {
        INTERACTION_WITH_SEARCHAGENT,
        REQUEST_TO_TUTORS_WITH_TITLE,
        RESPONSE_FROM_TUTORS_ABOUT_TITLE,
        REQUEST_TO_TUTORS_WITH_CHAPTERS,
        RESPONSE_FROM_TUTORS_WITH_ADDITINF,
        SELECTION_COURSE,
        DONE
    }

    private List<List<String>> result = null;
    private List<List<Chapter>> resultChapter = null;
    public String recomendedCourses = "";
    private boolean finish = false;

    private class RequestPerformer extends Behaviour {

        private String courseTitle;
        private boolean[] complexity;
        private int duration;
        private double price;
        private String[] chapters;

        protected Step stepm = Step.INTERACTION_WITH_SEARCHAGENT;
        protected MessageTemplate mt; // The template to receive replies
        protected ACLMessage msg;
        protected int repliesCnt = 0;
        protected List<AID> tutors;
        protected List<AID> deleteTutors = new ArrayList<>();

        public RequestPerformer() {

        }

        public RequestPerformer(String courseTitle, boolean[] complexity, int duration, double price, String[] chapters) {
            this.courseTitle = courseTitle;
            this.complexity = complexity;
            this.duration = duration;
            this.price = price;
            this.chapters = toLowerCase(chapters);
        }

        private String[] toLowerCase(String[] chapters) {

            for (int i = 0; i < chapters.length; i++) {
                chapters[i] = chapters[i].trim().toLowerCase();
            }

            return chapters;
        }

        @Override
        public void action() {

            switch (stepm) {

                case INTERACTION_WITH_SEARCHAGENT: {
                    if (interactionWithSA()) {
                        stepm = Step.REQUEST_TO_TUTORS_WITH_TITLE;
                    } else {
                        stepm = Step.DONE;
                    }
                    break;
                }

                case REQUEST_TO_TUTORS_WITH_TITLE: {
                    requestToTutorsWithTitle();
                    stepm = Step.RESPONSE_FROM_TUTORS_ABOUT_TITLE;
                    break;
                }

                case RESPONSE_FROM_TUTORS_ABOUT_TITLE: {
                    if (responseAboutTitle())
                        stepm = Step.REQUEST_TO_TUTORS_WITH_CHAPTERS;
                    break;
                }

                case REQUEST_TO_TUTORS_WITH_CHAPTERS: {

                    if (requestWithChapters()) {
                        stepm = Step.RESPONSE_FROM_TUTORS_WITH_ADDITINF;
                        resultChapter = new ArrayList<>();
                    }
                    break;
                }

                case RESPONSE_FROM_TUTORS_WITH_ADDITINF: {
                    if (responceWithAddInf())
                        stepm = Step.SELECTION_COURSE;
                    break;
                }

                case SELECTION_COURSE: {
                    System.out.println("\tSelection");
                    if (resultChapter.size() > 0) {
                        for (int r = 0; r < resultChapter.size(); r++) {
                            for (Chapter chapter : resultChapter.get(r)) {
                                System.out.println("\t" + chapter.getTitle() + " " + chapter.getPrice());
                            }
                        }
                        sortChapters = new ArrayList<List<Chapter>>(chapters.length);

                        for(int i = 0; i < chapters.length; i++) {
                            sortChapters.add(new ArrayList<>());
                        }

                        for (int i = 0; i < chapters.length; i++) {

                            for (int r = 0; r < resultChapter.size(); r++) {

                                for (int c = 0; c < resultChapter.get(r).size(); c++) {

                                    Chapter temp = resultChapter.get(r).get(c);

                                    if (temp.getTitle().trim().toLowerCase().contains(chapters[i]))
                                        sortChapters.get(i).add(temp);
                                }

                            }
                        }

                        for (int i = 0; i < sortChapters.size(); i++) {
                            for (int c = 0; c < sortChapters.get(i).size(); c++) {
                                System.out.print(sortChapters.get(i).get(c).getTitle() + " ["  + sortChapters.get(i).get(c).getTutorName() + "] ; ");
                            }
                            System.out.println();
                        }

                        select = new ArrayList<>(chapters.length);
                        for (int i = 0; i < chapters.length; i++) {
                            select.add(new Chapter());
                        }
                        level = 0;
                        try {
                            method();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(StudentAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        String str = "You searched \"" + courseTitle + "\" course with chapters:\n";
                        for (String chapter : chapters) {
                            str += "\"" + chapter + "\" ";
                        }

                        str += "\nComplexity:";
                        if (complexity[0]) str += "Beginner ";
                        if (complexity[1]) str += "Medium ";
                        if (complexity[2]) str += "Advanced ";

                        str += "\nTime constraints: " + duration + " hours";
                        str += "\nPrice constraints: " + price + " dollars";

                        if (selectedCourses.size() > 0) {
                            str += "\n\nRecommended course:";
                            for (List<Chapter> list : selectedCourses) {
                                str += "\n";
                                int totalDuration = 0;
                                double totalPrice = 0.0;
                                for (Chapter chapter : list) {
                                    str += "\n" + chapter.getTitle() + " Complexity: " + chapter.getComplexity() + " (" + chapter.getDuration() + " hours, " + chapter.getPrice() + " dollars)";
                                    str += "\nTutor: " + chapter.getTutorName();
                                    totalDuration += chapter.getDuration();
                                    totalPrice += chapter.getPrice();
                                }
                                str += "\nTotal duration: " + totalDuration + " hours";
                                str += "\nTotal price: " + totalPrice + " dollars";
                            }
                        }

                        recomendedCourses = str;
                        finish = true;
                        stepm = Step.DONE;
                    }
                    break;
                }

                case DONE: {
                    break;
                }
            }
        }

        private void method() throws InterruptedException {
            for (int i = 0; i < sortChapters.get(level).size(); i++) {
                //Thread.sleep(500);

                //for (int t = 0; t < level; t++) {
                //    System.out.print("\t");
                //}
                //System.out.print(sortChapters.get(level).get(i) + "\n");
                select.set(level, sortChapters.get(level).get(i));

                if (!sumInf()) {
                    //select.remove(level);
                    //continue;
                }

                if (level + 1 < chapters.length) {
                    level++;
                    method();
                } else {
                    writeSelected();
                    List<Chapter> tempCopy;
                    selectedCourses.add(cloneList(select));
                    //selectedCourses.clear();
                }
            }
            level--;
        }

        private List<Chapter> cloneList(List<Chapter> chapterList) {
            List<Chapter> clonedList = new ArrayList<Chapter>(chapterList.size());
            for (Chapter chapter : chapterList) {
                clonedList.add(new Chapter(chapter));
            }
            return clonedList;
        }

        private int level = 0;
        private List<List<Chapter>> sortChapters;
        private List<Chapter> select;
        private List<List<Chapter>> selectedCourses = new ArrayList<List<Chapter>>();

        private boolean sumInf() {

            int tduration = 0;
            int tprice = 0;

            for (Chapter c : select) {
                tprice += c.getPrice();
                tduration += c.getDuration();

                if (tprice > price || tduration > duration) {
                    if (tduration > duration) {
                        System.out.println("Цена дороже " + tprice);
                    }
                    if (tduration > duration) {
                        System.out.println("Время перебор " + tduration);
                    }
                    return false;
                }
            }
            return true;
        }

        private void writeSelected() {
            for (int i = 0; i < chapters.length; i++) {
                System.out.print(select.get(i).getTitle() + " " + select.get(i).getTutorName() + " ||| ");
            }
            System.out.print("\n");
        }

        private boolean responceWithAddInf() {
            ACLMessage reply = myAgent.receive(mt);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.INFORM) {
                    List<String> replyChapter = null;
                    try {
                        replyChapter = (List<String>) reply.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    List<Chapter> listChapter = convertToChapters(replyChapter);
                    resultChapter.add(listChapter);
                    System.out.println("I GET REPLY WITH ADD INF " + resultChapter.size());
                }
                repliesCnt++;
                if (repliesCnt >= tutors.size()) {
                    repliesCnt = 0;
                    return true;
                }
            }
            block();
            return false;
        }

        private List<Chapter> convertToChapters(List<String> resultChapter) {

            //0 - Title
            //1 - Duration
            //2 - Price
            //3 - Complexity
            //4 - CourseId
            //5 - LocalName
            List<Chapter> chapters = new ArrayList<>(resultChapter.size());
            for (String strChapter : resultChapter) {
                String[] ary = strChapter.split(":");
                chapters.add(new Chapter(
                        ary[0], Integer.parseInt(ary[1]), Double.parseDouble(ary[2]), ary[3], Integer.parseInt(ary[4]), ary[5]
                ));
                System.out.println(ary[0] + " " + ary[5]);
            }

            return chapters;
        }

        private boolean requestWithChapters() {
            ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

            for (AID tutor : tutors) {
                order.addReceiver(tutor);
            }

            try {
                order.setContentObject(chapters);
            } catch (IOException e) {
                e.printStackTrace();
            }
            order.setConversationId("courses");
            order.setReplyWith("order" + System.currentTimeMillis());
            System.out.println("requestWithChapters");
            send(order);

            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("courses"),
                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));

            return true;
        }

        private boolean responseAboutTitle() {
            ACLMessage reply = myAgent.receive(mt);
            if (reply != null) {
                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                    String content = reply.getContent();
                    System.out.println("I GET REPLY ABOUT COURSES " + content);
                } else if (reply.getPerformative() == ACLMessage.REFUSE) {
                    deleteTutors.add(reply.getSender());
                    System.out.println("AADD DELETE");
                }
                repliesCnt++;
                if (repliesCnt >= tutors.size()) {
                    repliesCnt = 0;
                    if (deleteTutors.size() != 0) {
                        deleteTutors();
                    }
                    return true;
                }
            }

            block();
            return false;
        }

        private void deleteTutors() {
            for (AID deleteTutor : deleteTutors) {
                for (AID tutor : tutors) {
                    if (tutor.getName().equals(deleteTutor.getName())) {
                        tutors.remove(tutor);
                        break;
                    }
                }
            }
            deleteTutors.clear();
        }

        private void requestToTutorsWithTitle() {
            msg = new ACLMessage(ACLMessage.CFP);
            tutors.forEach(msg::addReceiver);

            msg.setContent(this.courseTitle);
            msg.setConversationId("courses");
            msg.setReplyWith("cfp" + System.currentTimeMillis());
            System.out.println("Send request to daugh");
            myAgent.send(msg);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("courses"),
                    MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
        }

        protected boolean interactionWithSA() {
            createSearchAgent();
            System.out.println("Prepare message");
            ACLMessage msg = prepareMessageToSA();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            myAgent.send(msg);

            ACLMessage receiveMsg = blockingReceive();
            getTutorsFromMsg(receiveMsg);

            return tutors.size() != 0;
        }

        protected void getTutorsFromMsg(ACLMessage receiveMsg) {
            tutors = null;
            try {
                tutors = (List<AID>) receiveMsg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            System.out.println("Tutors: ");
            for (AID tem : tutors) {
                System.out.println("\t" + tem.getName());

            }
        }

        protected ACLMessage prepareMessageToSA() {
            AID search = new AID();
            search.setName("SearchAgent@machine:1099/JADE");
            System.out.println(search.getName());
            ACLMessage msg2 = new ACLMessage(ACLMessage.CFP);
            msg2.setSender(getAID());
            msg2.addReceiver(search);
            msg2.setContent("Tutor");
            return msg2;
        }

        protected void createSearchAgent() {
            try {
                getContainerController().createNewAgent("SearchAgent", "com.b1ngoos.student.agent.SearchAgent", null).start();
                System.out.println("SearchAgentCreated");
            } catch (StaleProxyException ex) {
                Logger.getLogger(StudentAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public boolean done() {
            if (stepm == Step.DONE) {
                System.out.println(getName() + " DONE");
            }
            return stepm == Step.DONE;
        }
    }


    private class CoursesBehaviour extends RequestPerformer {

        private boolean done = false;

        @Override
        public void action() {

            switch (stepm) {

                case INTERACTION_WITH_SEARCHAGENT: {
                    if (interactionWithSA()) {
                        stepm = Step.REQUEST_TO_TUTORS_WITH_TITLE;
                    } else {
                        stepm = Step.DONE;
                    }
                    break;
                }

                case REQUEST_TO_TUTORS_WITH_TITLE: {
                    msg = new ACLMessage(ACLMessage.REQUEST);
                    for (AID tutor : tutors) {
                        msg.addReceiver(tutor);
                    }
                    msg.setConversationId("courses");
                    msg.setReplyWith("request" + System.currentTimeMillis());
                    System.out.println("Send request to daugh");
                    myAgent.send(msg);

                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("courses"),
                            MessageTemplate.MatchInReplyTo(msg.getReplyWith()));
                    result = new ArrayList<List<String>>();
                    stepm = Step.RESPONSE_FROM_TUTORS_ABOUT_TITLE;
                    break;
                }

                case RESPONSE_FROM_TUTORS_ABOUT_TITLE: {
                    System.out.println("ESPONSE_FROM_TUTORS_ABOUT_TITLE");
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            try {
                                result.add((List<String>) reply.getContentObject());
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                            System.out.println("I GET REPLY ABOUT COURSES " + result.size());
                        }
                        repliesCnt++;
                        if (repliesCnt >= tutors.size()) {
                            //received all replies
                            repliesCnt = 0;
                            stepm = Step.DONE;
                            done = true;
                        }
                    } else {
                        block();
                    }
                    break;
                }
                case REQUEST_TO_TUTORS_WITH_CHAPTERS:
                    break;
                case RESPONSE_FROM_TUTORS_WITH_ADDITINF:
                    break;
                case SELECTION_COURSE:
                    break;
                case DONE:
                    break;
            }
        }

        @Override
        public boolean done() {
            return done;
        }
    }

    public boolean startBehaviour(String courseTitle, boolean[] complexity, int duration, double price, String[] chapters) {

        recomendedCourses = "";
        finish = false;
        addBehaviour(new RequestPerformer(courseTitle, complexity, duration, price, chapters));
        do {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } while (!finish);
        return true;
    }

    public boolean startBehaviour() {
        result = null;
        addBehaviour(new CoursesBehaviour());
         do {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } while (result == null);

        return true;
    }

    public List<List<String>> getResult() {
        return result;
    }

    public void setup() {

        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(Main.class);
            }
        }.start();
        Main startUpTest = Main.waitForStartUpTest();

        startUpTest.setAgent(this);

        System.out.println("Registered");
    }
}
