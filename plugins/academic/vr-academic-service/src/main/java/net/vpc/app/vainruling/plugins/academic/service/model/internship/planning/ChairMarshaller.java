package net.vpc.app.vainruling.plugins.academic.service.model.internship.planning;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.IntegerGene;

/**
 * Created by vpc on 5/20/16.
 */
public class ChairMarshaller extends BaseMarshaller {

    public ChairMarshaller(int activity, int index, Configuration conf) {
        super(activity, index, conf);
    }

    @Override
    public Gene marshall(PlanningActivityTableExt activityTable) throws InvalidConfigurationException {
        return new IntegerGene(getConf(), 0, activityTable.getTable().getChairs().size() - 1);
    }

    @Override
    public void unmarshall(IChromosome iChromosome, PlanningActivityTableExt activityTable) {
        Object allele = (iChromosome.getGene(getIndex())).getAllele();
        String chair = allele == null ? null : activityTable.getTable().getChairs().get((int) allele);
        activityTable.getTable().getActivity(getActivity()).setChair(chair);
    }
}
