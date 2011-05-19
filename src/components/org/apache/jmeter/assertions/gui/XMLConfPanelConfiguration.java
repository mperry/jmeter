package org.apache.jmeter.assertions.gui;

public interface XMLConfPanelConfiguration {

	public boolean isWhitespace();
	public boolean isValidating();
	public boolean isTolerant();
	public boolean isNamespace();
	public boolean isQuiet();
	public boolean showWarnings();
	public boolean reportErrors();
	public boolean isDownloadDTDs();

	
	public void setValidating(boolean b);
	public void setWhitespace(boolean b);
	public void setTolerant(boolean b);
    public void setNamespace(boolean b);
    public void setShowWarnings(boolean b);
    public void setReportErrors(boolean b);
    public void setQuiet(boolean b);
    public void setDownloadDTDs(boolean b);
    
}
