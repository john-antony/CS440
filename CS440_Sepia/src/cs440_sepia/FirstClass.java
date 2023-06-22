package cs440_sepia;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;

public class FirstClass extends Agent {

	public FirstClass(int arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public Map initialStep(StateView arg0, HistoryView arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void loadPlayerData(InputStream arg0) {
		// TODO Auto-generated method stub

	}

	public Map middleStep(StateView arg0, HistoryView arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void savePlayerData(OutputStream arg0) {
		// TODO Auto-generated method stub

	}

	public void terminalStep(StateView arg0, HistoryView arg1) {
		// TODO Auto-generated method stub

	}

//	public void display(Stateview stateview) {
//		List<Integer> unitIDs = stateview.getUnitIds(playernum);
//
//		for (Integer unitID : unitIDs) {
//			UnitView unitView = stateview.getUnit(unitID);
//
//			TemplateView templateView = unitView.getTemplateView();
//
//			System.out.println(templateView.getName() + ": " + unitID);
//		}
//	}
//
}
