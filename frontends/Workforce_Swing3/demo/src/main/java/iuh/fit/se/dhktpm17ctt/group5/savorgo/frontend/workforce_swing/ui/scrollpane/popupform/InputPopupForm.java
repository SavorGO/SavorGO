package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform;

import java.io.IOException;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;

/**
 * Interface for input popup forms.
 */
public interface InputPopupForm {
    
    /**
     * Validates the input fields of the form.
     *
     * @return True if all inputs are valid, false otherwise.
     */
    public boolean validateInput();

    /**
     * Retrieves the data from the form.
     *
     * @return An Object containing the data collected from the form.
     * @throws IOException 
     * @throws BusinessException 
     */
    public Object getData() throws BusinessException, IOException;
}