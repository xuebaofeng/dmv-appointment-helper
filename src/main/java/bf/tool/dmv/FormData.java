package bf.tool.dmv;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author Baofeng Xue at 2015/11/18 17:33.
 */
class FormData {
    private String[] args;
    private String dl;
    private String[] nameSplit;
    private String[] birthSplit;
    private String[] phoneSplit;

    public FormData(String... args) {
        this.args = args;
    }

    public String getDl() {
        return dl;
    }

    public String[] getNameSplit() {
        return nameSplit;
    }

    public String[] getBirthSplit() {
        return birthSplit;
    }

    public String[] getPhoneSplit() {
        return phoneSplit;
    }

    public FormData invoke() {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Main")
                .defaultHelp(true).description("CA dmv.");
        parser.addArgument("-n", "--name").help("name, John-Smith");
        parser.addArgument("-b", "--birth").help("birth, 1988-08-08");
        parser.addArgument("-dl", "--dl").help("permit number, A1111111");
        parser.addArgument("-p", "--phone").help("phone number, 111-111-1111");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        String name = ns.getString("name");
        String birth = ns.getString("birth");
        dl = ns.getString("dl");
        String phone = ns.getString("phone");

        nameSplit = name.split("-");
        birthSplit = birth.split("-");
        phoneSplit = phone.split("-");
        return this;
    }
}
