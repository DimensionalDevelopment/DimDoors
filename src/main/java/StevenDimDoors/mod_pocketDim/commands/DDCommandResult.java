package StevenDimDoors.mod_pocketDim.commands;

public class DDCommandResult {
	
	
	public static final DDCommandResult SUCCESS = new DDCommandResult(0, "", false);
	public static final DDCommandResult TOO_FEW_ARGUMENTS = new DDCommandResult(1, "Error: Too few arguments passed to the command", true);
	public static final DDCommandResult TOO_MANY_ARGUMENTS = new DDCommandResult(2, "Error: Too many arguments passed to the command", true);
	public static final DDCommandResult INVALID_DIMENSION_ID = new DDCommandResult(3, "Error: Invalid dimension ID", true);
	public static final DDCommandResult UNREGISTERED_DIMENSION = new DDCommandResult(4, "Error: Dimension is not registered", false);
	public static final DDCommandResult INVALID_ARGUMENTS = new DDCommandResult(5, "Error: Invalid arguments passed to the command.", true);
	
	public static final int CUSTOM_ERROR_CODE = -1;
	
	private int code;
	private String message;
	private boolean printUsage;
	
	private DDCommandResult(int code, String message, boolean printUsage)
	{
		this.code = code;
		this.message = message;
		this.printUsage = printUsage;
	}
	
	public DDCommandResult(String message)
	{
		this(CUSTOM_ERROR_CODE, message, false);
	}
	
	public DDCommandResult(String message, boolean printUsage)
	{
		this(CUSTOM_ERROR_CODE, message, printUsage);
	}
	
	public boolean failed()
	{
		return (code != 0);
	}
	
	public int getCode()
	{
		return code;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public boolean shouldPrintUsage()
	{
		return printUsage;
	}
}
