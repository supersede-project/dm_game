package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.ProcessManager;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

public class RequirementsEditingMethod implements DMMethod
{
    public static final String NAME = "Edit Requirements";

    private String name;
    private List<DMRoleSpec> list;
    private List<DMOption> options;

    public RequirementsEditingMethod()
    {
        this.name = NAME;
        list = new ArrayList<>();
        options = new ArrayList<>();
    }

    @Override
    public String getName()
    {
        return this.name;
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
                if (mgr.getRequirementsCount() == 0)
                {
                    return false;
                }

                for (Requirement r : mgr.requirements())
                {
                    if (r.getStatus() != RequirementStatus.Editable.getValue())
                    {
                        return false;
                    }
                }

                return true;
            }
        });
        return list;
    }

    @Override
    public String getPage(ProcessManager mgr)
    {
        return "edit_requirements";
    }

}
