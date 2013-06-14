package at.tugraz.ist.wv.diagnose;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.widget.Button;
 
public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
    public static AlertDialog showAlertDialog(Context context, String title, String message) {
    	
    	ContextThemeWrapper contextW = new ContextThemeWrapper(context, R.style.AppBaseTheme);
        AlertDialog alertDialog = new AlertDialog.Builder(contextW).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
         	 
        // Showing Alert Message
        //alertDialog.show();
        return alertDialog;
    }

    public static void nuthin(AlertDialog alertDialog)
    {
            // Setting alert dialog icon
            //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        	alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });	

    }
    
	public static void showErrorAlertDialog(Context context, String errorId, boolean status) {
		
		String title = "An Error occured";
		String message = "";
		
		if (errorId.equals("ACCOUNT_MISSING"))
		{
			title = context.getString(R.string.solve_or_skip);
			message = context.getString(R.string.solve_text);	
		}
		
		showAlertDialog(context, title, message);
		
	}
}