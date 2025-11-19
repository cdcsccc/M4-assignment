package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates a StatementPrinter.
     *
     * @param invoice invoice containing performances
     * @param plays   map of play information
     */
    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return formatted invoice statement
     * @throws RuntimeException if a play type is unknown
     */
    public String statement() {

        int totalAmount = 0;
        int volumeCredits = 0;

        // local constant to avoid magic number 100
        final int cents = 100;

        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer()
                        + System.lineSeparator());

        final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

        for (final Performance p : invoice.getPerformances()) {

            final Play play = plays.get(p.getPlayID());
            int thisAmount;

            switch (play.getType()) {
                case "tragedy":
                    thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += (p.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD)
                                * Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON;
                    }
                    break;

                case "comedy":
                    thisAmount = Constants.COMEDY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                                + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                    }
                    thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * p.getAudience();
                    break;

                default:
                    throw new RuntimeException(
                            String.format("unknown type: %s", play.getType()));
            }

            // Add volume credits
            volumeCredits += Math.max(
                    p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);

            if ("comedy".equals(play.getType())) {
                volumeCredits += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }

            // Print line for this performance
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    formatter.format(thisAmount / cents),
                    p.getAudience()));

            totalAmount += thisAmount;
        }

        result.append(String.format(
                "Amount owed is %s%n", formatter.format(totalAmount / cents)));
        result.append(String.format(
                "You earned %s credits%n", volumeCredits));

        return result.toString();
    }
}
