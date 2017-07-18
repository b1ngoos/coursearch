package com.b1ngoos.student.agent;

import java.io.IOException;
import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class SearchAgent extends Agent {

	private class SearchBehaviour extends OneShotBehaviour {
        @Override
        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }

        @Override
		public void action() {
			ACLMessage receiveMsg = blockingReceive();
			if (receiveMsg != null) {
				String agentType = receiveMsg.getContent();

				ACLMessage responseMsg = new ACLMessage(ACLMessage.INFORM);
				responseMsg.addReceiver(receiveMsg.getSender());

				List<AID> tutors = getTutors(agentType);

				System.out.println("\t\tFound " + tutors.size() + " tutors");

				try {
					responseMsg.setContentObject((Serializable) tutors);
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(responseMsg);
				System.out.println("\t\tSend to " + receiveMsg.getSender());
			} else
				block();
			System.out.println("\t\tOne-shot");
		}

		private List<AID> getTutors(String type) {

			List<AID> tutors = convertToAID(searchAgents(type));

			return tutors;
		}

		private List<AID> convertToAID(DFAgentDescription[] searchAgents) {

			List<AID> tutors = new ArrayList<>();

			for (int i = 0; i < searchAgents.length; i++) {
				tutors.add(searchAgents[i].getName());
			}

			return tutors;
		}

		private DFAgentDescription[] searchAgents(String type) {
			DFAgentDescription[] result = null;
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(type);
			dfd.addServices(sd);

			try {
				result = DFService.search(myAgent, dfd);

			} catch (FIPAException ex) {
				Logger.getLogger(Owner.class.getName()).log(Level.SEVERE, null, ex);
			}
			return result;
		}
		
	}
	
	public void setup() {
		System.out.println("\t Search agent setup: " + getName());
		addBehaviour(new SearchBehaviour());
	}

}
