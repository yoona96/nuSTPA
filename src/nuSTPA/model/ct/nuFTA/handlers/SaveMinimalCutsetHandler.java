package nuSTPA.model.ct.nuFTA.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import nuSTPA.model.ct.nuFTA.controller.MainController;
import nuSTPA.model.ct.nuFTA.parts.AbstractFaultTreePart;
import nuSTPA.model.ct.nuFTA.parts.FaultTreePart;
import nuSTPA.model.ct.nuFTA.parts.TemplatePart;
import nuSTPA.model.ct.nuFTA.parts.TimeTemplatePart;

public class SaveMinimalCutsetHandler {
	@Execute
	public void execute(EPartService partService){
		for (MPart m : partService.getParts()) {
			if (m.getLabel().equals(MainController.getInstance().getSelectedPart())) {
				if (m.getObject() instanceof FaultTreePart) {
					FaultTreePart ftPart = (FaultTreePart) m.getObject();
					ftPart.saveMinimalCutset();
					break;
				} else if (m.getObject() instanceof TimeTemplatePart) {
					TimeTemplatePart timeTemplatePart = (TimeTemplatePart) m.getObject();
					timeTemplatePart.saveMinimalCutset();
					break;
				}
			}
		}
	}
}
