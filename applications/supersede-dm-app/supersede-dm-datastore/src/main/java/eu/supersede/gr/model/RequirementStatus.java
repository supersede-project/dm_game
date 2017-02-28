package eu.supersede.gr.model;

import java.util.HashSet;
import java.util.Set;

public enum RequirementStatus
{
    Unconfirmed(0), Editable(1), Confirmed(2), Enacted(3), Discarded(4);
    private Integer value;

    RequirementStatus(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return this.value;
    }

    public static RequirementStatus valueOf(Integer status)
    {
        switch (status)
        {
            case 0:
                return Unconfirmed;
            case 1:
                return Editable;
            case 2:
                return Confirmed;
            case 3:
                return Enacted;
            case 4:
                return Discarded;
            default:
                return Unconfirmed;
        }
    }

    public static Set<RequirementStatus> next(RequirementStatus status)
    {
        Set<RequirementStatus> set = new HashSet<RequirementStatus>();
        switch (status)
        {
            case Unconfirmed:
                set.add(RequirementStatus.Editable);
                set.add(RequirementStatus.Discarded);
                break;
            case Editable:
                set.add(RequirementStatus.Confirmed);
                break;
            case Confirmed:
                set.add(RequirementStatus.Enacted);
                break;
            case Discarded:
                break;
            case Enacted:
                break;
        }
        return set;
    }
}