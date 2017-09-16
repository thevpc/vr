package net.vpc.app.vainruling.plugins.academic.service.model.internship.planning;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.IntegerGene;

/**
 * Created by vpc on 5/20/16.
 */
public class SpaceTimeMarshaller extends BaseMarshaller {

    public SpaceTimeMarshaller(int activity, int index, Configuration conf) {
        super(activity, index, conf);
    }

    @Override
    public Gene marshall(PlanningActivityTableExt activityTable) throws InvalidConfigurationException {
        return new IntegerGene(getConf(), 0, activityTable.getSpaceTimes().size() - 1);
    }

    @Override
    public void unmarshall(IChromosome iChromosome, PlanningActivityTableExt activityTable) {
        Object allele = (iChromosome.getGene(getIndex())).getAllele();
        if(allele==null){
            return;
        }
        int selected = (int) allele;
        PlanningSpaceTime value = activityTable.getSpaceTimes().get(selected);

        PlanningActivity activity = activityTable.getTable().getActivities().get(getActivity());
        if (!activity.isFixedSpace() && !activity.isFixedTime()) {
            activity.setSpaceTime(value);
        } else if (activity.isFixedSpace()) {
            activity.setSpaceTime(new PlanningSpaceTime(activity.getSpaceTime().getRoom(), value.getTime()));
        } else if (activity.isFixedTime()) {
            activity.setSpaceTime(new PlanningSpaceTime(activity.getSpaceTime().getRoom(), activity.getSpaceTime().getTime()));
        }
    }
}
