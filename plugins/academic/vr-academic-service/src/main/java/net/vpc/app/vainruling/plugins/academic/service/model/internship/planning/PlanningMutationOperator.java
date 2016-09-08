package net.vpc.app.vainruling.plugins.academic.service.model.internship.planning;

import org.jgap.*;
import org.jgap.data.config.Configurable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 5/27/16.
 */
public class PlanningMutationOperator extends BaseGeneticOperator implements Configurable {
    /**
     * String containing the CVS revision. Read out via reflection!
     */
    private final static String CVS_REVISION = "$Revision: 1.45 $";
    PlanningActivityTableExt table;
    PlanningFitnessFunction fitness;
    /**
     * Calculator for dynamically determining the mutation rate. If set to
     * null the value of m_mutationRate will be used. Replaces the previously used
     * boolean m_dynamicMutationRate.
     */
//    private IUniversalRateCalculator m_mutationRateCalc;

    private PlanningMutationOperatorConfigurable m_config = new
            PlanningMutationOperatorConfigurable();
//    /**
//     * Constructs a new instance of this MutationOperator without a specified
//     * mutation rate, which results in dynamic mutation being turned on. This
//     * means that the mutation rate will be automatically determined by this
//     * operator based upon the number of genes present in the chromosomes.
//     * <p>
//     * Attention: The configuration used is the one set with the static method
//     * Genotype.setConfiguration.
//     *
//     * @throws InvalidConfigurationException
//     *
//     * @author Neil Rotstan
//     * @author Klaus Meffert
//     * @since 1.0
//     */
//    public PlanningMutationOperator()
//            throws InvalidConfigurationException {
//        this(Genotype.getStaticConfiguration());
//    }

//    /**
//     * @param a_conf the configuration to use
//     * @throws InvalidConfigurationException
//     *
//     * @author Klaus Meffert
//     * @since 3.0
//     */
//    public PlanningMutationOperator(final Configuration a_conf,PlanningActivityTableExt table)
//            throws InvalidConfigurationException {
//        super(a_conf);
//        this.table=table;
//        setMutationRateCalc(new DefaultMutationRateCalculator(a_conf));
//    }

//    /**
//     * Constructs a new instance of this MutationOperator with a specified
//     * mutation rate calculator, which results in dynamic mutation being turned
//     * on.
//     * @param a_config the configuration to use
//     * @param a_mutationRateCalculator calculator for dynamic mutation rate
//     * computation
//     * @throws InvalidConfigurationException
//     *
//     * @author Klaus Meffert
//     * @since 1.1
//     */
//    public PlanningMutationOperator(final Configuration a_config,
//                            final IUniversalRateCalculator
//                                    a_mutationRateCalculator)
//            throws InvalidConfigurationException {
//        super(a_config);
//        setMutationRateCalc(a_mutationRateCalculator);
//    }

    /**
     * Constructs a new instance of this MutationOperator with the given
     * mutation rate.
     *
     * @param a_config              the configuration to use
     * @param a_desiredMutationRate desired rate of mutation, expressed as
     *                              the denominator of the 1 / X fraction. For example, 1000 would result
     *                              in 1/1000 genes being mutated on average. A mutation rate of zero disables
     *                              mutation entirely
     * @throws InvalidConfigurationException
     * @author Neil Rotstan
     * @since 1.1
     */
    public PlanningMutationOperator(final Configuration a_config,
                                    final int a_desiredMutationRate, PlanningActivityTableExt table)
            throws InvalidConfigurationException {
        super(a_config);
        this.table = table;
        this.fitness = new PlanningFitnessFunction(table);
        m_config.m_mutationRate = a_desiredMutationRate;
    }

    /**
     * @param a_population           the population of chromosomes from the current
     *                               evolution prior to exposure to any genetic operators. Chromosomes in this
     *                               array should not be modified. Please notice, that the call in
     *                               Genotype.evolve() to the implementations of GeneticOperator overgoes this
     *                               due to performance issues
     * @param a_candidateChromosomes the pool of chromosomes that have been
     *                               mutated
     * @author Neil Rotstan
     * @author Klaus Meffert
     * @since 1.0
     */
    public void operate(final Population a_population,
                        final List a_candidateChromosomes) {
        if (a_population == null || a_candidateChromosomes == null) {
            return;
        }
        if (m_config.m_mutationRate == 0) {
            return;
        }
        // Determine the mutation rate. If dynamic rate is enabled, then
        // calculate it using the IUniversalRateCalculator instance.
        // Otherwise, go with the mutation rate set upon construction.
        // -------------------------------------------------------------
        boolean mutate = false;
        RandomGenerator generator = getConfiguration().getRandomGenerator();
        // It would be inefficient to create copies of each Chromosome just
        // to decide whether to mutate them. Instead, we only make a copy
        // once we've positively decided to perform a mutation.
        // ----------------------------------------------------------------
        int size = Math.min(getConfiguration().getPopulationSize(),
                a_population.size());
        IGeneticOperatorConstraint constraint = getConfiguration().
                getJGAPFactory().getGeneticOperatorConstraint();
        for (int i = 0; i < size; i++) {
            IChromosome chrom = a_population.getChromosome(i);

            mutate = (generator.nextInt(m_config.m_mutationRate) == 0);
            if (mutate) {
                table.unmarshall(chrom);
                IChromosome copyOfChromosome = null;
                try {
                    mutateTable();
                    copyOfChromosome = table.marshall(getConfiguration());
                } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                if (copyOfChromosome != null) {
                    a_candidateChromosomes.add(copyOfChromosome);
                }
            }
        }
    }

    private void mutateTable() throws InvalidConfigurationException {
        Map<String, PlanningTeacherStats> t = fitness.evalTeacherStats(table.getTable(), false);
        HashSet<String> missingExaminer = new HashSet<>();
        HashSet<String> surplusExaminer = new HashSet<>();
        HashSet<String> missingChair = new HashSet<>();
        HashSet<String> surplusChair = new HashSet<>();
        for (Map.Entry<String, PlanningTeacherStats> e : t.entrySet()) {
            PlanningTeacherStats s = e.getValue();
            if (s.chairBalance < 0) {
                missingChair.add(s.teacherName);
            } else if (s.chairBalance > 0) {
                surplusChair.add(s.teacherName);
            }
            if (s.examinerBalance < 0) {
                missingExaminer.add(s.teacherName);
            } else if (s.examinerBalance > 0) {
                surplusExaminer.add(s.teacherName);
            }
        }
        RandomGenerator generator = getConfiguration().getRandomGenerator();
        boolean switchExaminer = missingExaminer.size() > 0 && surplusExaminer.size() > 0;
        boolean switchChair = missingChair.size() > 0 && surplusChair.size() > 0;
        if (switchExaminer && switchChair) {
            if (generator.nextBoolean()) {
                switchExaminer = false;
            } else {
                switchChair = false;
            }
        }
        if (switchExaminer) {
            String[] missingExaminerArr = missingExaminer.toArray(new String[missingExaminer.size()]);
            String[] surplusExaminerArr = surplusExaminer.toArray(new String[surplusExaminer.size()]);
            String a = missingExaminerArr[generator.nextInt(missingExaminer.size())];
            String b = surplusExaminerArr[generator.nextInt(surplusExaminer.size())];
            //switch these examiners!
            List<PlanningActivity> surplusExaminersActivities = table.getExaminerActivities(b);
            PlanningActivity bc = surplusExaminersActivities.get(generator.nextInt(surplusExaminersActivities.size()));
            bc.setExaminer(a);
        }
        if (switchChair) {
            String[] missingChairArr = missingChair.toArray(new String[missingChair.size()]);
            String[] surplusChairArr = surplusChair.toArray(new String[surplusChair.size()]);
            String a = missingChairArr[generator.nextInt(missingChair.size())];
            String b = surplusChairArr[generator.nextInt(surplusChair.size())];
            //switch these Chairs!
            List<PlanningActivity> surplusChairsActivities = table.getChairActivities(b);
            PlanningActivity bc = surplusChairsActivities.get(generator.nextInt(surplusChairsActivities.size()));
            bc.setChair(a);
        }

    }

    /**
     * Helper: mutate all atomic elements of a gene.
     *
     * @param a_gene      the gene to be mutated
     * @param a_generator the generator delivering amount of mutation
     * @author Klaus Meffert
     * @since 1.1
     */
    private void mutateGene(final Gene a_gene, final RandomGenerator a_generator) {

        for (int k = 0; k < a_gene.size(); k++) {
            // Retrieve value between 0 and 1 (not included) from generator.
            // Then map this value to range -1 and 1 (-1 included, 1 not).
            // -------------------------------------------------------------
            double percentage = -1 + a_generator.nextDouble() * 2;
            // Mutate atomic element by calculated percentage.
            // -----------------------------------------------
            a_gene.applyMutation(k, percentage);
        }
    }

    /**
     * Compares this GeneticOperator against the specified object. The result is
     * true if and the argument is an instance of this class and is equal wrt the
     * data.
     *
     * @param a_other the object to compare against
     * @return true: if the objects are the same, false otherwise
     * @author Klaus Meffert
     * @since 2.6
     */
    public boolean equals(final Object a_other) {
        try {
            return compareTo(a_other) == 0;
        } catch (ClassCastException cex) {
            return false;
        }
    }

    /**
     * Compares the given GeneticOperator to this GeneticOperator.
     *
     * @param a_other the instance against which to compare this instance
     * @return a negative number if this instance is "less than" the given
     * instance, zero if they are equal to each other, and a positive number if
     * this is "greater than" the given instance
     * @author Klaus Meffert
     * @since 2.6
     */
    public int compareTo(Object a_other) {
        if (a_other == null) {
            return 1;
        }
        PlanningMutationOperator op = (PlanningMutationOperator) a_other;
        if (m_config.m_mutationRate != op.m_config.m_mutationRate) {
            if (m_config.m_mutationRate > op.m_config.m_mutationRate) {
                return 1;
            } else {
                return -1;
            }
        }
        // Everything is equal. Return zero.
        // ---------------------------------
        return 0;
    }

    public int getMutationRate() {
        return m_config.m_mutationRate;
    }

    /**
     * @param a_mutationRate new rate of mutation, expressed as
     *                       the denominator of the 1 / X fraction. For example, 1000 would result
     *                       in 1/1000 genes being mutated on average. A mutation rate of zero disables
     *                       mutation entirely
     * @author Klaus Meffert
     * @since 3.2.2
     */
    public void setMutationRate(int a_mutationRate) {
        m_config.m_mutationRate = a_mutationRate;
    }

    class PlanningMutationOperatorConfigurable
            implements java.io.Serializable {
        /**
         * The current mutation rate used by this MutationOperator, expressed as
         * the denominator in the 1 / X ratio. For example, X = 1000 would
         * mean that, on average, 1 / 1000 genes would be mutated. A value of zero
         * disables mutation entirely.
         */
        public int m_mutationRate;
    }
}
