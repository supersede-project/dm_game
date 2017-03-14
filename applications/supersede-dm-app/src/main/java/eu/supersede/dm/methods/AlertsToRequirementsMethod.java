package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;

public class AlertsToRequirementsMethod implements DMMethod
{
    private static final String NAME = "Convert Alerts To Requirements";
    private static final String PAGE = "convert_alerts_to_requirements";

    private List<DMRoleSpec> list;
    private List<DMOption> options;

    public AlertsToRequirementsMethod()
    {
        list = new ArrayList<>();
        options = new ArrayList<>();
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public DMObjective getObjective()
    {
        return DMObjective.PrioritizeRequirements;
    }

    @Override
    public List<DMRoleSpec> getRoleList()
    {
        return list;
    }

    public String getPage(String step)
    {
        return "";
    }

    @Override
    public List<DMOption> getOptions()
    {
        return this.options;
    }

    public void setOption(String optName, String optValue)
    {
        // TODO
    }

    public void init(ProcessManager status)
    {
        // String pid = executor.startBPMN( "supersedeAHPDM" );
        // status.setProperty( "pid", pid );
    }

    @Override
    public List<DMCondition> preconditions()
    {
        List<DMCondition> list = new ArrayList<DMCondition>();

        list.add(new DMCondition()
        {
            @Override
            public boolean isTrue(ProcessManager mgr)
            {
                return mgr.getAlerts().size() > 0;
            }
        });

        return list;
    }

    @Override
    public String getPage(ProcessManager mgr)
    {
        return PAGE;
    }

	@Override
	public String getDescription(ProcessManager arg0) {
		return "Convert Alerts To Requirements";
	}

	@Override
	public String getLabel(ProcessManager arg0) {
		return "Convert Alerts To Requirements";
	}
}