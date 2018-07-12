package net.vpc.app.vainruling.plugins.academic.service.model.internship.planning;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.IntegerGene;

/**
 * Created by vpc on 5/20/16.
 */
public class ExaminerMarshaller extends BaseMarshaller {

    public ExaminerMarshaller(int activity, int index, Configuration conf) {
        super(activity, index, conf);
    }

    @Override
    public Gene marshall(PlanningActivityTableExt activityTable) throws InvalidConfigurationException {
        return new IntegerGene(getConf(), 0, activityTable.getTable().getExaminers().size() - 1);
    }

    @Override
    public void unmarshall(IChromosome iChromosome, PlanningActivityTableExt activityTable) {
        Object allele = (iChromosome.getGene(getIndex())).getAllele();
        String examiner = allele == null ? null : activityTable.getTable().getExaminers().get((int) allele);
        activityTable.getTable().getActivity(getActivity()).setExaminer(examiner);
    }
}
