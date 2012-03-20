package suncertify.gui;

import suncertify.domain.SubContractor;
import suncertify.service.SearchCriteria;
import suncertify.service.ServiceFactory;
import suncertify.service.SubContractorServiceLocal;

public class DetailDialogTest {
	
	public static void main(String[] args) {
		SubContractorServiceLocal serLoc = (SubContractorServiceLocal) ServiceFactory.getInstance().getLocalService();
		try {
			SubContractor sc = serLoc.search(new SearchCriteria()).get(13);
			new DetailDialog(sc, new SubContractorController(new SubContractorModel(), serLoc));
		} catch (Exception e) {
			System.err.println("Test fehlgeschlagen!");
			e.printStackTrace();
		}
	}
}
