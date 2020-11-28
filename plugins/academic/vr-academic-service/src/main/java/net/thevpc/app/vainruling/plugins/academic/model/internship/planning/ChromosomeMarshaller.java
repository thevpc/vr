package net.thevpc.app.vainruling.plugins.academic.model.internship.planning;

import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;

/**
 * Created by vpc on 5/20/16.
 */
public interface ChromosomeMarshaller {
    Gene marshall(PlanningActivityTableExt activityTable) throws InvalidConfigurationException;

    void unmarshall(IChromosome iChromosome, PlanningActivityTableExt activityTable);
}
