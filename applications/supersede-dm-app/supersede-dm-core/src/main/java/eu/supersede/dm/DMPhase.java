package eu.supersede.dm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DMPhase
{

    String name;

    Map<String, DMPhase> nexts = new HashMap<>();
    Map<String, DMPhase> prevs = new HashMap<>();

    public DMPhase(String name)
    {

        this.name = name;

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addNext(DMPhase phase)
    {
        nexts.put(phase.getName(), phase);
        phase.prevs.put(this.name, this);
    }

    public Collection<DMPhase> getNextPhases()
    {
        return nexts.values();
    }

    public Collection<DMPhase> getPrevPhases()
    {
        return prevs.values();
    }

    public boolean isAllowed(ProcessManager mgr)
    {
        try
        {
            checkPreconditions(mgr);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public void checkPreconditions(ProcessManager mgr) throws Exception
    {
        // By default, no constraints on the transition exist
    }

    public void activate(ProcessManager mgr)
    {
        // To be overridden by child classes
    }

}
