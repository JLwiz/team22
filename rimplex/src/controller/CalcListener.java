package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.Timer;

import GUI.ButtonPadPanel;
import GUI.CalculatorDisplay;
import GUI.HistoryWindow;
import GUI.PlaybackWindow;
import calculations.ComplexNumber;
import calculations.Equation;
import html.HTMLText;
import html.HelpDisplay;
import printing.PrintableHistory;
import printing.PrinterController;
import util.InputParser;
import util.Playback;

/**
 * Listener class to handle events of buttons, events of the JFrame, and other components of the
 * GUI.
 * 
 * This Work complies with the JMU honor code
 * 
 * @author Andrew Fryer, Storm Behrens
 * @version 2.0 (04/12/2021)
 *
 */
public class CalcListener implements ActionListener, KeyListener, WindowListener
{
  private static CalcListener listener;
  private CalculatorDisplay frame;
  private Equation evaluate;
  private InputParser parser;
  private Playback playback = null;
  private final String leftParen = "(";
  private final String rightParen = ")";

  private String recording = "";
  private String record = null;
  private final String backspace = "backspace";
  private final String equals = "equals";
  private final String openParenthases = "open parenthases";
  private final String closedParenthases = "closed parenthases";
  private final String one = "1", two = "2", three = "3", four = "4", five = "5", six = "6",
      seven = "7", eight = "8", nine = "9", zero = "0";
  private final String addText = "add", subtractText = "subtract", multiplyText = "multiply",
      divideText = "divide", decimalText = "decimal", exponentText = "exponent";
  private final String imaginaryConstant = "i";
  private final String sineText = "sin", cosineText = "cos", tangentText = "tan";
  private final String aboutText = "About";
  private final String helpText = "Display Help";
  private int mode = 0;

  /**
   * Default Constructor.
   */
  private CalcListener()
  {
    evaluate = Equation.getInstance();
    parser = InputParser.getInstance();
  } // Default Constructor.

  /**
   * gives the instance of the listener.
   * 
   * @return CalcListener - the listener
   */

  public static CalcListener getInstance()
  {
    if (listener == null)
      listener = new CalcListener();
    return listener;
  }

  /**
   * Determines what actions to do following an action event.
   */
  @Override
  public void actionPerformed(final ActionEvent e)
  {
    frame = CalculatorDisplay.getInstance();

    if (e.getSource() instanceof JButton)
      try
      {
        buttonActions((JButton) e.getSource());
      }
      catch (IOException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

    if (e.getSource() instanceof JMenuItem)
      try
      {
        menuActions((JMenuItem) e.getSource());
      }
      catch (IOException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

    if (e.getSource() instanceof Timer)
    {
      Timer timer = (Timer) e.getSource();

      if (timer.getDelay() != 750)
      {
        HistoryWindow window = HistoryWindow.getInstance();
        boolean state = window.isOpen();

        if (state)
          if (window.getWidth() != 200)
            window.setSize(window.getWidth() + 10, 300);
        if (!state)
          if (window.getWidth() != 0)
            window.setSize(window.getWidth() - 10, 300);

        if (window.getWidth() == 0 || window.getWidth() == 200)
          timer.stop();
      }
      else
        try
        {
          playback.run();
        }
        catch (IOException e1)
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

    }
  } // actionPerformed method.

  /**
   * keyPressed - Will perform the correct action of the key pressed.
   * 
   * @param e
   *          (KeyEvent)
   */
  @Override
  public void keyTyped(final KeyEvent e)
  {
    ButtonPadPanel pad = ButtonPadPanel.getInstance();
    int code = e.getKeyChar();

    switch (code)
    {
      case 8:
        pad.pressButton(backspace);
        break;
      case 10: // enter
        pad.pressButton(equals);
        break;
      case 22:
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        String copy = "";
        try
        {
          copy = (String) c.getData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException e1)
        {

        }
        catch (IOException e1)
        {

        }
        catch (NullPointerException e1)
        {

        }
        frame.getInputField().setText(frame.getInputField().getText() + copy);
        break;
      case 40:
        pad.pressButton(openParenthases);
        break;
      case 41:
        pad.pressButton(closedParenthases);
        break;
      case 42:
        pad.pressButton(multiplyText);
        break;
      case 43: // + add
        pad.pressButton(addText);
        break;
      case 45: // - minus
        pad.pressButton(subtractText);
        break;
      case 46:
        pad.pressButton(decimalText);
        break;
      case 47:
        pad.pressButton(divideText);
        break;
      case 48:
        pad.pressButton(zero);
        break;
      case 49:
        pad.pressButton(one);
        break;
      case 50:
        pad.pressButton(two);
        break;
      case 51:
        pad.pressButton(three);
        break;
      case 52:
        pad.pressButton(four);
        break;
      case 53:
        pad.pressButton(five);
        break;
      case 54:
        pad.pressButton(six);
        break;
      case 55:
        pad.pressButton(seven);
        break;
      case 56:
        pad.pressButton(eight);
        break;
      case 57:
        pad.pressButton(nine);
        break;
      case 94:
        pad.pressButton(exponentText);
        break;
      case 105:
        pad.pressButton(imaginaryConstant);
        break;
      default: // a key was pressed that we don't allow
        break;
    }

  } // keyPressed method.

  /**
   * append - Will add a character to the end of the display.
   * 
   * @param addition
   *          ( String )
   */
  private void append(final char addition)
  {
    String text = frame.getInputField().getText();
    if (addition != 'b')
    {
      frame.setInput(text + addition);
      return;
    }
    if (text.length() > 0)
      frame.setInput(text.substring(0, text.length() - 1));
  } // append method.

  /**
   * buttonActions - Will call the buttons actions.
   * 
   * @param button
   *          The button pressed.
   * @throws IOException
   *           - the IOException
   */
  private void buttonActions(final JButton button) throws IOException
  {
    numberActions(button);
    operationActions(button);
    logicActions(button);
    historyActions(button);
    playbackActions(button);
  } // buttonActions method.

  /**
   * clears InputField for CalculatorDisplay.
   */
  private void clearInput()
  {
    frame.clearInputField();
  }

  /**
   * changes the mode of the calculator between fractions and decimals.
   * 
   * @throws IOException
   *           - the IOException
   */

  private void changeMode() throws IOException
  {
    mode = ButtonPadPanel.getInstance().updateMode();
  }

  /**
   * formats input text for display.
   * 
   * **Should not modify input in a way that would change parse result**
   * 
   * @param input
   *          - the initial input
   * @return String - the formatted input
   */
  private String formatInput(final String input)
  {
    String text = input;
    String iPlus = "+i";
    String iMinus = "-i";
    String iParen = "(i";
    char imaginary = 'i';
    if (!text.endsWith(rightParen) || !text.startsWith(leftParen))
    {
      text = leftParen + text + rightParen;
    }
    if (text.contains(iPlus) || text.contains(iMinus) || text.contains(iParen))
    {
      text = text.replace(iParen, "(1" + imaginary);
      text = text.replace(iPlus, "+1" + imaginary);
      text = text.replace(iMinus, "-1" + imaginary);
    }
    return text;
  }

  /**
   * returns the proper ComplexNumber text based off the mode of the calculator (Variable
   * inFractions). (either in decimal or fractional)
   * 
   * @param compNum
   *          - the complex number to get the string of
   * @return String - the proper text for the Complex Number
   */

  private String getComplexText(final ComplexNumber compNum)
  {
    String text = "";
    if (mode == 0)
    {
      text = compNum.toString();
    }
    else if (mode == 1)
    {
      text = compNum.toFraction();
    }
    else if (mode >= 2)
    {
      text = compNum.toPolar();
    }
    return text;
  }

  /**
   * historyActions - Will call the button actions for history.
   * 
   * @param button
   *          The button pressed.
   */
  private void historyActions(final JButton button)
  {
    switch (button.getName().toLowerCase())
    {
      case "history":
        if (!HistoryWindow.getInstance().isOpen())
          HistoryWindow.getInstance().toggleHistory(true);
        break;
      case "winhistory": // window history button.
        if (HistoryWindow.getInstance().isOpen())
          HistoryWindow.getInstance().toggleHistory(false);
        break;
      default:
        break;
    }
  } // historyActions method.

  /**
   * signals that the input is invalid.
   */
  private void invalidInput()
  {
    frame.invalidStatus(true, "Invalid Input");
  }

  /**
   * checks if a character is a number or number adjacent symbol.
   * 
   * @param letter
   *          - the symbol to check
   * @return boolean - whether the character is a number
   */

  private boolean isNumber(final char letter)
  {
    String numbers = "1234567890.i";
    boolean isNum = false;
    for (int i = 0; i < numbers.length(); i++)
    {
      if (numbers.charAt(i) == letter)
      {
        isNum = true;
      }
    }
    return isNum;
  }

  /**
   * Sets the default language to the language selected and refreshes the JFrame.
   * 
   * @param language
   *          - the language to change to
   * @throws IOException
   *           - the IOException
   */

  private void languageActions(final String language) throws IOException
  {
    if (language.equals("En"))
    {
      Locale.setDefault(Locale.forLanguageTag("en-US"));
      frame.changeLanguage();
    }
    else if (language.equals("Sp"))
    {
      Locale.setDefault(Locale.forLanguageTag("es-ES"));
      frame.changeLanguage();
    }
    else if (language.equals("Ger"))
    {
      Locale.setDefault(Locale.forLanguageTag("de-DE"));
      frame.changeLanguage();
    }
    else if (language.equals("Fr"))
    {
      Locale.setDefault(Locale.forLanguageTag("fr-FR"));
      frame.changeLanguage();
    }
    HistoryWindow.getInstance().newFrame();
    PlaybackWindow.getInstance().newFrame();
  }

  /**
   * logicActions - Will call the button actions for logic.
   * 
   * @param button
   *          The button pressed.
   * @throws IOException
   *           - the IOException
   */
  private void logicActions(final JButton button) throws IOException
  {
    switch (button.getName().toLowerCase())
    {
      case "inverse":
      case "logarithm":
      case "conjugate":
      case "squareroot":
      case sineText:
      case cosineText:
      case tangentText:
      case "realpart":
      case "imaginarypart":
        if (evaluate.operatorEmpty())
        {
          if (evaluate.getFirstOp() == null
              || frame.getInputField() != null && !frame.getInputField().getText().equals(""))
          {
            operatorButton(button.getText());
          }
          else
          {
            operationsProcessor(evaluate.getFirstOp().getRawString(), button.getText());
          }
        }
        else
        {
          frame.invalidStatus(true, "Can't get the " + button.getName() + ".");
        }
        HistoryWindow.getInstance().addToHistory(frame.getDisplay().getText());
        break;
      default:
        break;
    }
  } // logicActions method.

  /**
   * menuActions - Will perform the menu actions.
   * 
   * @param menu
   *          The menu item selected.
   * @throws IOException
   *           - the IOException
   */
  private void menuActions(final JMenuItem menu) throws IOException
  {
    String name = menu.getName();
    if (name.equals("Print History"))
    {
      HistoryWindow history = HistoryWindow.getInstance();
      PrintableHistory ph = new PrintableHistory(history.getTextArea());
      PrinterController.print(ph, frame);
    }
    else if (name.equals("Playback"))
    {
      PlaybackWindow.getInstance().setVisible(true);
    }
    else if (menu.getText().equals("English") || menu.getText().equals("Espa�ol")
        || menu.getText().equals("Deutsche") || menu.getText().equals("Fran�aise"))
    {
      languageActions(menu.getName());
    }
    else if (name.equals(aboutText))
    {
      JEditorPane editor = new JEditorPane();
      JFrame aboutFrame = new JFrame(aboutText);
      ImageIcon logo = new ImageIcon(CalcListener.class.getResource("/logo/icon.png"));
      aboutFrame.setIconImage(logo.getImage());
      editor.setSize(200, 300);
      editor.setEditable(false);
      editor.setContentType("text/html");
      String img = CalcListener.class.getResource("/resources/images/logoRimplex.png").toString();
      editor.setText("<html><img src=\"" + img + "\" width=200 height=50>" + HTMLText.ABOUTPAGE);
      aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      aboutFrame.setContentPane(editor);
      aboutFrame.pack();
      aboutFrame.setVisible(true);
    }
    else if (name.equals(helpText))
    {
      HelpDisplay display = new HelpDisplay();
      display.displayHelp();
    }
  } // menuActions method.

  /**
   * numberActions - Will call button actions for number buttons.
   * 
   * @param button
   *          The button pressed.
   */
  private void numberActions(final JButton button)
  {
    switch (button.getName().toLowerCase())
    {
      case zero:
      case one:
      case two:
      case three:
      case four:
      case five:
      case six:
      case seven:
      case eight:
      case nine:
      case decimalText:
      case openParenthases:
      case closedParenthases:
        append(button.getText().charAt(0));
        break;
      case imaginaryConstant:
        append('i');
        break;
      case backspace:
        append('b');
        break;
      case "sign":
        signChange();
        break;
      default:
        break;
    }
  } // numberActions method.

  /**
   * operationActions - Will call button actions for operations.
   * 
   * @param button
   *          The button pressed.
   * @throws IOException
   *           - the IOException
   */
  private void operationActions(final JButton button) throws IOException
  {
    switch (button.getName().toLowerCase())
    {
      case addText:
      case divideText:
      case multiplyText:
      case subtractText:
      case exponentText:
        operationsSwitch(button.getText().trim());
        break;
      case equals:
        operatorButton(button.getText());
        HistoryWindow.getInstance().addToHistory(frame.getDisplay().getText());
        break;
      case "clear":
        clearInput();
        frame.invalidStatus(false, "no Error");
        break;
      case "reset":
        resetDisplay();
        break;
      case "mode":
        changeMode();
        break;
      default:
        break;
    }
  } // operationActions method.

  /**
   * Logic processor for performing operations and updating the display.
   * 
   * @param input
   *          - the text from the inputField
   * @param operation
   *          - the operation thats taking place
   * @throws NumberFormatException
   *           the Input was invalid.
   */
  private void operationsProcessor(final String input, final String operation)
      throws NumberFormatException
  {
    JLabel display = frame.getDisplay();
    String text = input;
    if (input != null && !input.isEmpty())
    {
      text = formatInput(input);
    }
    String equalsOperator = "=";
    String logOperator = "log";

    if (evaluate.operatorEmpty() && text.length() > 0 && !input.equals("Undefined"))
    {
      ComplexNumber op1;
      if (evaluate.getFirstOp() == null || text.length() > 0)
      {
        op1 = parser.parseInput(text);
      }
      else
      {

        op1 = evaluate.getFirstOp();
      }

      frame.setDisplay("");
      if (operation.equals(equalsOperator))
      {
        frame.setDisplay(text + operation + getComplexText(op1));
      }
      // put the inverse sign here when the button is added.
      else if (operation.equals("Inv"))
      {
        if (op1 != null || (input != null && input.isEmpty()))
        {
          op1 = parser.parseInput(text);
        }
        ComplexNumber inv = op1.inverse();
        String str = "1/" + getComplexText(op1) + equalsOperator + getComplexText(inv);
        op1 = inv;
        frame.setDisplay(str);
      }
      else if (operation.equals("Con"))
      {
        String test = frame.getInputField().getText();
        if (op1 != null && test.isBlank())
        {
          ComplexNumber firstOp = evaluate.getFirstOp();
          op1 = new ComplexNumber(firstOp.getReal(), firstOp.getImaginary());
        }
        else if (op1 != null || (input != null && input.isEmpty()))
        {
          op1 = parser.parseInput(text);
        }
        String str = operation + getComplexText(op1) + equalsOperator;
        ComplexNumber conj = op1.conjugate();
        op1 = conj;
        str += op1;
        frame.setDisplay(str);
      }
      else if (operation.equals(sineText) || operation.equals(cosineText)
          || operation.equals(tangentText) || operation.equals("<html>&#8730</html>")
          || operation.equals(logOperator))
      {
        evaluate.setOperator(operation);
        evaluate.setFirstOp(op1);
        ComplexNumber unitaryResult = evaluate.solve();
        String complexText = getComplexText(unitaryResult);
        frame.setDisplay(
            operation + op1.toString() + equalsOperator + complexText);
        op1 = unitaryResult;
        
      }
      else if (operation.equals("Re"))
      {
        String str = operation + getComplexText(op1) + equalsOperator;
        op1 = new ComplexNumber(op1.getReal(), 0.0);
        str += getComplexText(op1);
        frame.setDisplay(str);
      }
      else if (operation.equals("Im"))
      {
        String str = operation + getComplexText(op1) + equalsOperator;
        op1 = new ComplexNumber(0.0, op1.getImaginary());
        str += getComplexText(op1);
        frame.setDisplay(str);
      }
      else
      {
        frame.setDisplay(text + operation);
        evaluate.setOperator(operation);
      }
      evaluate.setFirstOp(op1);
      clearInput();
    }
    else if (!evaluate.operandEmpty() && evaluate.operatorEmpty() && text.length() == 0)
    {
      frame.setDisplay("");
      if (operation.equals(equalsOperator))
      {
        frame.setDisplay(getComplexText(evaluate.getFirstOp()) + operation
            + getComplexText(evaluate.getFirstOp()));
      }
      else
      {
        evaluate.setOperator(operation);
        frame.setDisplay(getComplexText(evaluate.getFirstOp()) + operation);
      }
    }
    else if (!evaluate.operandEmpty() && !evaluate.operatorEmpty() && text.length() > 0)
    {
      ComplexNumber op2 = parser.parseInput(text);
      evaluate.setSecondOp(op2);
      evaluate.solve();
      if (operation.equals(equalsOperator))
      {
        frame.setDisplay(
            display.getText() + text + operation + getComplexText(evaluate.getFirstOp()));
      }
      else
      {
        frame.setDisplay(getComplexText(evaluate.getFirstOp()) + operation);
        evaluate.setOperator(operation);
      }
      clearInput();
    }
    else
    {
      invalidInput();
    }

  }

  /**
   * checks if their are open parenthesis and acts with the operator accordingly. if the parenthesis
   * are open it appends the operator to the text, else it performs the operation.
   * 
   * @param operation
   *          - the operator to act on
   */

  private void operationsSwitch(final String operation)
  {
    String negative = " -";
    if (openParenCheck() || (frame.getInputField().getText().length() == 0
        && operation.contentEquals(negative.trim()) && !evaluate.operatorEmpty()))
    {
      if (operation.charAt(0) == '+' && frame.getInputField().getText().contains("+"))
      {
        invalidInput();
      }
      else
      {
        append(operation.charAt(0));
      }
    }
    else
    {
      if (operation.equals("^")) // acts like the number Buttons but may become a proper operator
                                 // later
      {
        append(operation.charAt(0));
      }
      else
      {
        operatorButton(operation);
      }
    }
  }

  /**
   * Processes the event for the operation buttons. (add,subtract,multiply,divide,equals)
   * 
   * @param operation
   *          - the operation thats taking place
   */

  private void operatorButton(final String operation)
  {
    JLabel inputField = frame.getInputField();
    String text;
    text = inputField.getText();
    if (text == null)
      text = "";
    if ((evaluate.operandEmpty() || !evaluate.operatorEmpty()) && text.length() == 0)
    {
      invalidInput();
    }
    else
    {
      try
      {
        operationsProcessor(text, operation);
        frame.invalidStatus(false, "no");
      }
      catch (NumberFormatException e)
      {
        invalidInput();
      }
    }
  }

  /**
   * returns if the textfield has unclosed parenthesis. true if their are more left paren then right
   * paren, returns false otherwise.
   * 
   * @return boolean - if there are open parenthesis
   */

  private boolean openParenCheck()
  {
    int openParen = 0;
    int closedParen = 0;
    String text = frame.getInputField().getText();
    for (int i = 0; i < text.length(); i++)
    {
      if (text.charAt(i) == '(')
      {
        openParen++;
      }
      if (text.charAt(i) == ')')
      {
        closedParen++;
      }
    }

    return (openParen != closedParen);
  }

  /**
   * playbackActions - Will perform the playback actions.
   * 
   * @param button
   *          The button pressed.
   * @throws IOException
   *           - the IOException
   */
  private void playbackActions(final JButton button) throws IOException
  {
    switch (button.getName().toLowerCase())
    {
      case "record":
        int place;
        PlaybackWindow.getInstance().toggleIcon();
        if (!PlaybackWindow.getInstance().recording())
        {
          place = HistoryWindow.getInstance().getPlace();
          HistoryWindow.getInstance().nextRecording();
          PlaybackWindow.getInstance()
              .saveRecording(HistoryWindow.getInstance().getRecording(place));
        }
        else
        {
          clearInput();
          resetDisplay();
          if (playback != null)
            playback.toggleFocusable(true);
        }
        break;
      case "open":
        record = PlaybackWindow.getInstance().getRecording();
        HistoryWindow.getInstance().clearHistory();
        break;
      case "play":
        /*
         * Grab a string based on the name of recording stored in the JComboBox, create a new object
         * of Playback with the string of total recording.
         */
        if (record == null || record.trim().equals(""))
          return;
        if (!recording.equals(record))
        {
          clearInput();
          resetDisplay();
          playback = new Playback(record);
          recording = record;
        }
        playback.pause(false);
        playback.start();
        break;
      case "pause":
        if (playback != null)
          if (!playback.paused())
            playback.pause(true);
        break;
      case "close":
        PlaybackWindow.getInstance().setVisible(false);
        if (playback != null)
        {
          clearInput();
          resetDisplay();
          playback.pause(true);
          playback.toggleFocusable(true);
          PlaybackWindow.getInstance().toggleRecord(true);
          recording = "";
          HistoryWindow.getInstance().clearHistory();
        }
        break;
      default:
        break;
    }
  } // playbackActions method.

  /**
   * resets the display and the equation class.
   */
  private void resetDisplay()
  {
    frame.setDisplay("");
    evaluate.setFirstOp(null);
    evaluate.setSecondOp(null);
    evaluate.setOperator(null);
  }

  private void signChange()
  {
    String text = "";
    if (frame.getInputField().getText() != null && frame.getInputField().getText().length() != 0)
    {
      text = frame.getInputField().getText();
      String neg = "-";
      String pos = "\\+";
      text = text.replaceAll(neg, neg + neg);
      text = text.replaceAll(pos, neg);
      text = text.replaceAll(neg + neg, pos);
      for (int i = 0; i < text.length(); i++)
      {
        if (isNumber(text.charAt(i)) && i == 0)
        {
          text = neg + text;
        }
        else if (text.charAt(i) == '+' && (i == 0 || !isNumber(text.charAt(i - 1))))
        {
          text = text.substring(0, i) + text.substring(i + 1);
        }
        else if (isNumber(text.charAt(i)) && i > 0 && (text.charAt(i - 1) == '('
            || text.charAt(i - 1) == '�' || text.charAt(i - 1) == '�'))
        {
          text = text.substring(0, i) + neg + text.substring(i);
        }
      }
      frame.setInput(text);
    }
  }

  @Override
  public void windowClosed(final WindowEvent e)
  {
    System.exit(0);
  } // windowClosed method.

  // ----------Unused Implemented Methods----------

  @Override
  public void windowOpened(final WindowEvent e)
  {
  } // unused.

  @Override
  public void windowClosing(final WindowEvent e)
  {
  } // unused.

  @Override
  public void windowIconified(final WindowEvent e)
  {
  } // unused.

  @Override
  public void windowDeiconified(final WindowEvent e)
  {
  } // unused.

  @Override
  public void windowDeactivated(final WindowEvent e)
  {
  } // unused.

  @Override
  public void windowActivated(final WindowEvent e)
  {
  } // unused.

  @Override
  public void keyPressed(final KeyEvent e)
  {
  } // unused.

  @Override
  public void keyReleased(final KeyEvent e)
  {
  } // unused.

} // CalcListener class.
