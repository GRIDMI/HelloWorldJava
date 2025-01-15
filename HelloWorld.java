// Core Interfaces
interface CharacterProvider {
    char getCharacter();
}

interface TextProvider {
    String getText();
}

interface Printer {
    void print(String text);
}

interface MessageFormatter {
    String formatMessage(String message);
}

// Enhanced Interfaces for Extensibility
interface TextSource extends TextProvider {
    void refreshSource();
}

interface PrintStrategy {
    void executePrint(String formattedMessage);
}

// Abstract Factories
interface CharacterProviderFactory {
    CharacterProvider createCharacterProvider(char character);
}

interface TextProviderFactory {
    TextProvider createTextProvider();
}

interface PrinterFactory {
    Printer createPrinter();
}

interface MessageFormatterFactory {
    MessageFormatter createMessageFormatter();
}

// Character Providers
class StaticCharacterProvider implements CharacterProvider {
    private final char character;

    public StaticCharacterProvider(char character) {
        this.character = character;
    }

    @Override
    public char getCharacter() {
        return character;
    }
}

class HelloWorldTextProvider implements TextProvider {
    private final CharacterProvider[] characterProviders;

    public HelloWorldTextProvider() {
        characterProviders = new CharacterProvider[] {
            new StaticCharacterProvider('H'), new StaticCharacterProvider('e'),
            new StaticCharacterProvider('l'), new StaticCharacterProvider('l'),
            new StaticCharacterProvider('o'), new StaticCharacterProvider(','),
            new StaticCharacterProvider(' '), new StaticCharacterProvider('W'),
            new StaticCharacterProvider('o'), new StaticCharacterProvider('r'),
            new StaticCharacterProvider('l'), new StaticCharacterProvider('d'),
            new StaticCharacterProvider('!')
        };
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (CharacterProvider cp : characterProviders) {
            sb.append(cp.getCharacter());
        }
        return sb.toString();
    }
}

// Core Implementations
class StandardOutputPrinter implements Printer, PrintStrategy {
    @Override
    public void print(String text) {
        System.out.println(text);
    }

    @Override
    public void executePrint(String formattedMessage) {
        print("[Strategy]: " + formattedMessage);
    }
}

class SimpleMessageFormatter implements MessageFormatter {
    @Override
    public String formatMessage(String message) {
        return "[Formatted]: " + message;
    }
}

// Factory Implementations for Dependency Injection
class DefaultCharacterProviderFactory implements CharacterProviderFactory {
    @Override
    public CharacterProvider createCharacterProvider(char character) {
        return new StaticCharacterProvider(character);
    }
}

class DefaultTextProviderFactory implements TextProviderFactory {
    @Override
    public TextProvider createTextProvider() {
        return new HelloWorldTextProvider();
    }
}

class DefaultPrinterFactory implements PrinterFactory {
    @Override
    public Printer createPrinter() {
        return new StandardOutputPrinter();
    }
}

class DefaultMessageFormatterFactory implements MessageFormatterFactory {
    @Override
    public MessageFormatter createMessageFormatter() {
        return new SimpleMessageFormatter();
    }
}

// Abstract Processor
abstract class AbstractMessageProcessor {
    protected TextProvider textProvider;
    protected Printer printer;
    protected MessageFormatter formatter;

    public AbstractMessageProcessor(TextProvider textProvider, Printer printer, MessageFormatter formatter) {
        this.textProvider = textProvider;
        this.printer = printer;
        this.formatter = formatter;
    }

    public abstract void processMessage();
}

// Enhanced Processor with Multiple Responsibilities
class EnhancedMessageProcessor extends AbstractMessageProcessor {
    public EnhancedMessageProcessor(TextProvider textProvider, Printer printer, MessageFormatter formatter) {
        super(textProvider, printer, formatter);
    }

    @Override
    public void processMessage() {
        if (textProvider instanceof TextSource) {
            ((TextSource) textProvider).refreshSource();
        }
        String message = textProvider.getText();
        String formattedMessage = formatter.formatMessage(message);
        if (printer instanceof PrintStrategy) {
            ((PrintStrategy) printer).executePrint(formattedMessage);
        } else {
            printer.print(formattedMessage);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TextProviderFactory textProviderFactory = new DefaultTextProviderFactory();
        PrinterFactory printerFactory = new DefaultPrinterFactory();
        MessageFormatterFactory formatterFactory = new DefaultMessageFormatterFactory();

        AbstractMessageProcessor messageProcessor = new MessageProcessorBuilder()
            .setTextProvider(textProviderFactory.createTextProvider())
            .setPrinter(printerFactory.createPrinter())
            .setFormatter(formatterFactory.createMessageFormatter())
            .build();

        messageProcessor.processMessage();
    }
}
