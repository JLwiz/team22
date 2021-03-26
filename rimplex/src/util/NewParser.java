package util;

import calculations.ComplexAddition;
import calculations.ComplexDivision;
import calculations.ComplexMultiplication;
import calculations.ComplexNumber;
import calculations.ComplexSubtraction;

/**
 * Utility class that validates and parses input for the ComplexNumber calculator.
 * 
 * This work complies with the JMU Honor Code.
 * 
 * @author Storm Behrens
 * @version 03/26/2021
 */

public class NewParser
{
  private static NewParser parser = new NewParser();
  private final String validCharacters = "0123456789.+-i*/()";
  private final String negative = "-";
  private final String imaginary = "i";
  private final String plus = "+";
  private final String multiply = "*";
  private final String divide = "/";
  private ComplexAddition add = new ComplexAddition();
  private ComplexSubtraction sub = new ComplexSubtraction();
  private ComplexMultiplication multi = new ComplexMultiplication();
  private ComplexDivision div = new ComplexDivision();

  /**
   * Default Constructor for InputParser.
   */

  private NewParser()
  {
  }

  /**
   * Formats imaginary numbers to allow for proper parsing.
   * 
   * @param input
   *          - the input to format
   * @return String - the formatted input
   */

  public String formatImaginary(final String input)
  {
    String text = input;
    String defaultNum = "1.0";
    if (text.contains(imaginary))
    {
      if (text.length() == 1)
      {
        text = defaultNum + text;
      }
      else if (text.length() == 2 && text.contains(negative))
      {
        text = negative + defaultNum + imaginary;
      }
    }

    return text;
  }

  /**
   * returns the instance of the parser.
   * 
   * @return InputParser - the parser for the program
   */

  public static NewParser getInstance()
  {
    return parser;
  }

  /**
   * method to check if input has parentheses.
   * 
   * @param input
   *          - input to check
   * @return boolean - if the input has parentheses
   */

  private boolean hasParentheses(final String input)
  {
    return (input.contains(")") || input.contains("("));
  }

  /**
   * parses a piece of the input and returns a complex number.
   * 
   * @param input
   *          - the input to parse
   * @return ComplexNumber - the resulting complex number
   * @throws NumberFormatException
   *           - if the input is invalid
   */

  private ComplexNumber parseComplex(final String input) throws NumberFormatException
  {
    if (input == null || input.length() == 0 || !validInput(input))
    {
      throw new NumberFormatException();
    }
    Double realNumber = 0.0;
    Double imaginaryNumber = 0.0;
    ComplexNumber number;
    String text = input;
    text = formatImaginary(text);
    if (text.contains(imaginary))
    {
      imaginaryNumber = Double.parseDouble(text.substring(0, text.length() - 1));
    }
    else
    {
      realNumber = Double.parseDouble(text);
    }
    number = new ComplexNumber(realNumber, imaginaryNumber);
    return number;

  }

  /**
   * parses an input and returns the resulting ComplexNumber.
   * 
   * This method reverses the order of operations for regression (regresses on "+,-" before "*,/")
   * This is so expressions linked through multiplication or division are properly isolated and
   * solved before including it with the rest of the equation.
   * 
   * @param input
   *          - the input to parse
   * @return ComplexNumber - the resulting complex number
   * @throws NumberFormatException
   *           - if the input is invalid
   */

  public ComplexNumber parseInput(final String input) throws NumberFormatException
  {
    if (input == null || input.length() == 0 || !validInput(input))
    {
      throw new NumberFormatException();
    }
    ComplexNumber n = null;
    String in = input.replaceAll("--", "");
    if (hasParentheses(in) && validParentheses(in))
    {
      for (int j = 0; j < 2; j++) // This loop is to allow for order of operations.
      {
        int leftP = 0;
        int rightP = 0;
        for (int i = 0; i < in.length(); i++) // This loop split input with multiple parentheses
                                              // apart
        {
          if (in.charAt(i) == '(')
          {
            leftP++;
          }
          if (in.charAt(i) == ')')
          {
            rightP++;
          }
          if (leftP == rightP && i < in.length() - 1)
          {
            if (j == 0) // first set of operations
            {
              if (in.charAt(i + 1) == '+')
              {
                n = add.calculate(parseInput(in.substring(0, i + 1)),
                    parseInput(in.substring(i + 2)));
              }
              else if (in.charAt(i + 1) == '-')
              {
                n = sub.calculate(parseInput(in.substring(0, i + 1)),
                    parseInput(in.substring(i + 2)));
              }
            }
            else if (j == 1) // second set of operations
            {
              if (in.charAt(i + 1) == '*')
              {
                n = multi.calculate(parseInput(in.substring(0, i + 1)),
                    parseInput(in.substring(i + 2)));
              }
              else if (in.charAt(i + 1) == '(') // requires separate check for input such as
                                                // '2(1+1i)'
              {
                n = multi.calculate(parseInput(in.substring(0, i + 1)),
                    parseInput(in.substring(i + 1)));
              }
              else if (in.charAt(i + 1) == '/')
              {
                n = div.calculate(parseInput(in.substring(0, i + 1)),
                    parseInput(in.substring(i + 2)));
              }
            }
          }
          else if (i == in.length() - 1 && j == 1)
          {
            if (n != null)
              break; // stops multiple regression calls
            n = parseInput(in.substring(1, in.length() - 1));
          }
        }
        if (n != null)
          break; // stops multiple regression calls
      }
    }
    else if (!hasParentheses(in)) // allows input without parentheses and allows regression
    {
      if (in.contains(plus))
      {
        String[] list = in.split("\\+");
        n = parseInput(list[0]);
        for (int i = 1; i < list.length; i++)
        {
          n = add.calculate(n, parseInput(list[i]));
        }
      }
      else if (in.contains(negative)) // requires special checks to account for negative numbers
      {
        String[] list = in.split(negative);
        int i = 1;
        if (list[0] == null || list[0].isBlank()) // checks if first number is negative
        {
          n = parseComplex(negative + list[1]);
          i = 2; // sets value of i to 2 so that the first number isn't counted twice
        }
        else
        {
          System.out.println(list[0]);
          n = parseInput(list[0]);
        }
        for (; i < list.length; i++)
        {
          n = sub.calculate(n, parseInput(list[i]));
        }
      }
      else if (in.contains(multiply))
      {
        String[] list = in.split("\\*");
        n = parseInput(list[0]);
        for (int i = 1; i < list.length; i++)
        {
          n = multi.calculate(n, parseInput(list[i]));
        }
      }
      else if (in.contains(divide))
      {
        String[] list = in.split(divide);
        n = parseInput(list[0]);
        for (int i = 1; i < list.length; i++)
        {
          n = div.calculate(n, parseInput(list[i]));
        }
      }
      else
      {
        n = parseComplex(in);
      }
    }
    else
    {
      throw new NumberFormatException();
    }
    return n;
  }

  /**
   * Checks to make sure only valid characters are in the input.
   * 
   * @param input
   *          - the string to check
   * @return boolean - whether the input is valid
   */

  private boolean validInput(final String input)
  {
    boolean isValid = true;
    char[] inputChars = input.toCharArray();
    if (input.endsWith(plus) || input.endsWith(negative) || input.endsWith(multiply)
        || input.endsWith(divide))
    {
      isValid = false;
    }
    else if (input.startsWith(plus) || input.startsWith(multiply) || input.startsWith(divide))
    {
      isValid = false;
    }
    for (char i : inputChars)
    {
      if (validCharacters.lastIndexOf(i) == -1)
      {
        isValid = false;
      }
    }
    return isValid;
  }

  /**
   * checks if Parentheses are set up properly.
   * 
   * @param input
   *          - the input to check
   * @return boolean - if the Parentheses are valid
   */

  private boolean validParentheses(final String input)
  {
    boolean valid = true;
    int leftP = 0;
    int rightP = 0;
    for (int i = 0; i < input.length(); i++)
    {
      if (input.charAt(i) == '(')
      {
        leftP++;
      }
      if (input.charAt(i) == ')')
      {
        rightP++;
      }
      if (rightP > leftP)
      {
        valid = false;
      }
    }
    if (leftP != rightP)
      valid = false;
    return valid;
  }
}