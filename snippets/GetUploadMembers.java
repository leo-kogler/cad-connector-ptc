package at.smartpart.resolve;

import java.util.ArrayList;
import java.util.List;

import com.ptc.cipjava.jxthrowable;
import com.ptc.pfc.pfcAssembly.Assembly;
import com.ptc.pfc.pfcComponentFeat.ComponentFeat;
import com.ptc.pfc.pfcFeature.FeatureType;
import com.ptc.pfc.pfcFeature.Features;
import com.ptc.pfc.pfcModel.Model;
import com.ptc.pfc.pfcModel.ModelDescriptor;
import com.ptc.pfc.pfcModel.ModelType;
import com.ptc.pfc.pfcSession.Session;
import com.ptc.pfc.pfcSolid.Solid;

import at.smartpart.beans.PRT;

public class GetUploadMembers {

	Model nextModel;
	Solid curSolid;
	private Session wfSession;
	Assembly memberAsm;

	List<PRT> listPrt = new ArrayList<PRT>();
	List<String> listAsm = new ArrayList<String>();

	public GetUploadMembers(Model curModel, Session wfSession) {

		try {
			System.out.println(curModel.GetFileName());
		} catch (jxthrowable e2) {
			e2.printStackTrace();
		}

		this.wfSession = wfSession;
		
		

		try {
			walkDownDeps(curModel);
		} catch (jxthrowable e) {
			e.printStackTrace();
			System.out.println("problem traversing tree " + e.toString());
		}

	}

	public void walkDownDeps(Model curModel) throws jxthrowable {
		
		if (curModel.GetType() == ModelType.MDL_ASSEMBLY) {			
			
			memberAsm = (Assembly) curModel;
			
			
			if (!listAsm.contains(curModel.GetFileName())) {
				listAsm.add(curModel.GetFileName());
			}
			

			Features comps;

			comps = memberAsm.ListFeaturesByType(Boolean.FALSE, FeatureType.FEATTYPE_COMPONENT);

			for (int index = 0; index < comps.getarraysize(); index++) {

				ComponentFeat compFeature = (ComponentFeat) comps.get(index);

				ModelDescriptor description = compFeature.GetModelDescr();

				nextModel = wfSession.GetModelFromDescr(description);
				
				walkDownDeps(nextModel);

			}

		} else {
			nextModel = curModel;

			curSolid = (Solid) nextModel;

			String fileName = curModel.GetFileName();
		
			listPrt.add(new PRT(nextModel.GetFileName(), memberAsm.GetFileName()));
			
			System.out.println("Filename : " + fileName);

		}
		
		for(PRT prt : listPrt) {
			System.out.println("traverse :" + prt.getPRT());
		}
	
	}

	public List<String> getASMList() {
		return this.listAsm;
	}

	public List<PRT> getPRTList() {
		return this.listPrt;
	}
	
	public void clearAll() {
		listAsm.clear();
		listPrt.clear();
	}
}
