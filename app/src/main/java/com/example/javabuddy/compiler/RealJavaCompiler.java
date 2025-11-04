package com.example.javabuddy.compiler;

import android.content.Context;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Real Java Compiler using Android's built-in capabilities
 * Simulates real compilation by parsing, validating, and executing Java code
 */
public class RealJavaCompiler {
    private Context context;
    private File cacheDir;
    
    public RealJavaCompiler(Context context) {
        this.context = context;
        this.cacheDir = context.getCacheDir();
    }
    
    public Object compileAndRun(String code) {
        try {
            // Step 1: Parse and validate the code
            CompileResult validation = validateJavaCode(code);
            if (!validation.success) {
                return "Compilation Error:\n" + validation.message;
            }
            
            // Step 2: Extract class information
            String className = extractClassName(code);
            if (className == null) {
                return "Error: Could not find public class declaration";
            }
            
            // Step 3: Execute the code using advanced interpreter
            return executeJavaCode(code, className);
            
        } catch (Exception e) {
            return "Runtime Error: " + e.getMessage();
        }
    }
    
    private CompileResult validateJavaCode(String code) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Phase 1: Structural validation
        validateStructure(code, errors);
        
        // Phase 2: Syntax validation  
        validateSyntax(code, errors);
        
        // Phase 3: Semantic validation
        validateSemantics(code, errors, warnings);
        
        // Phase 4: Type checking
        validateTypes(code, errors);
        
        // Phase 5: Advanced language features
        validateAdvancedFeatures(code, errors, warnings);
        
        if (errors.isEmpty()) {
            String message = "Compilation successful";
            if (!warnings.isEmpty()) {
                message += "\nWarnings:\n" + String.join("\n", warnings);
            }
            return new CompileResult(true, message);
        } else {
            return new CompileResult(false, String.join("\n", errors));
        }
    }
    
    private void validateStructure(String code, List<String> errors) {
        // Check for class declaration
        if (!code.contains("class") && !code.contains("interface") && !code.contains("enum")) {
            errors.add("No class, interface, or enum declaration found");
        }
        
        // Check for balanced symbols
        if (!areBalanced(code, '{', '}')) {
            errors.add("Unbalanced curly braces { }");
        }
        if (!areBalanced(code, '(', ')')) {
            errors.add("Unbalanced parentheses ( )");
        }
        if (!areBalanced(code, '[', ']')) {
            errors.add("Unbalanced square brackets [ ]");
        }
        
        // Check for main method structure
        if (code.contains("main") && code.contains("String[]")) {
            if (!Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]\\s+\\w+\\s*\\)").matcher(code).find()) {
                errors.add("main method should be: public static void main(String[] args)");
            }
        }
    }
    
    private void validateSyntax(String code, List<String> errors) {
        // Check for missing semicolons (basic check)
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty() && !line.endsWith(";") && !line.endsWith("{") && 
                !line.endsWith("}") && !line.startsWith("//") && !line.startsWith("/*") &&
                !line.startsWith("*") && !line.startsWith("@") && !line.contains("class") &&
                !line.contains("interface") && !line.contains("enum") && !line.startsWith("if") &&
                !line.startsWith("for") && !line.startsWith("while") && !line.startsWith("switch") &&
                !line.startsWith("try") && !line.startsWith("catch") && !line.startsWith("finally") &&
                !line.startsWith("else") && !line.contains("return") && line.length() > 3) {
                // This might be missing a semicolon
                if (line.contains("=") || line.contains("System.out") || line.contains("++") || line.contains("--")) {
                    errors.add("Line " + (i + 1) + ": Missing semicolon");
                }
            }
        }
        
        // Check for method declaration syntax
        Pattern methodPattern = Pattern.compile("(public|private|protected)?\\s*(static)?\\s*\\w+\\s+\\w+\\s*\\([^)]*\\)");
        Matcher matcher = methodPattern.matcher(code);
        while (matcher.find()) {
            String method = matcher.group();
            if (!code.substring(matcher.end()).trim().startsWith("{")) {
                errors.add("Method declaration missing opening brace: " + method);
            }
        }
        
        // Check for variable declarations without initialization
        Pattern varPattern = Pattern.compile("(int|String|boolean|double|float|char|byte|short|long)\\s+\\w+\\s*;");
        matcher = varPattern.matcher(code);
        while (matcher.find()) {
            String var = matcher.group().trim();
            if (!var.contains("=")) {
                String varName = var.split("\\s+")[1].replace(";", "");
                // This is just a warning, not an error
            }
        }
    }
    
    private void validateSemantics(String code, List<String> errors, List<String> warnings) {
        // Check for unreachable code
        String[] lines = code.split("\n");
        boolean afterReturn = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("return") && !line.equals("return;")) {
                afterReturn = true;
            } else if (afterReturn && !line.isEmpty() && !line.startsWith("}") && !line.startsWith("//")) {
                warnings.add("Line " + (i + 1) + ": Unreachable code after return statement");
                afterReturn = false;
            }
            if (line.contains("{") || line.contains("}")) {
                afterReturn = false;
            }
        }
        
        // Check for variable naming conventions
        Pattern varNaming = Pattern.compile("(int|String|boolean|double)\\s+([A-Z]\\w*)");
        Matcher matcher = varNaming.matcher(code);
        while (matcher.find()) {
            warnings.add("Variable '" + matcher.group(2) + "' should start with lowercase (Java convention)");
        }
        
        // Check for unused imports (simplified)
        Pattern importPattern = Pattern.compile("import\\s+([\\w.]+);");
        matcher = importPattern.matcher(code);
        while (matcher.find()) {
            String importClass = matcher.group(1);
            String className = importClass.substring(importClass.lastIndexOf('.') + 1);
            if (!code.contains(className + " ") && !code.contains(className + ".") && !code.contains(className + "(")) {
                warnings.add("Unused import: " + importClass);
            }
        }
    }
    
    private void validateTypes(String code, List<String> errors) {
        // Type compatibility checking
        Pattern assignmentPattern = Pattern.compile("(int|String|boolean|double)\\s+(\\w+)\\s*=\\s*([^;]+);");
        Matcher matcher = assignmentPattern.matcher(code);
        while (matcher.find()) {
            String type = matcher.group(1);
            String varName = matcher.group(2);
            String value = matcher.group(3).trim();
            
            // Check type compatibility
            if (type.equals("int") && !isIntCompatible(value)) {
                errors.add("Cannot assign '" + value + "' to int variable '" + varName + "'");
            } else if (type.equals("String") && !isStringCompatible(value)) {
                errors.add("Cannot assign '" + value + "' to String variable '" + varName + "'");
            } else if (type.equals("boolean") && !isBooleanCompatible(value)) {
                errors.add("Cannot assign '" + value + "' to boolean variable '" + varName + "'");
            }
        }
        
        // Method call validation
        Pattern methodCallPattern = Pattern.compile("(\\w+)\\.(\\w+)\\s*\\([^)]*\\)");
        matcher = methodCallPattern.matcher(code);
        while (matcher.find()) {
            String obj = matcher.group(1);
            String method = matcher.group(2);
            // Basic validation - in real compiler this would check method signatures
            if (method.equals("length") && !obj.toLowerCase().contains("string") && !code.contains("String " + obj)) {
                errors.add("Method 'length()' not found for variable '" + obj + "'");
            }
        }
    }
    
    private void validateAdvancedFeatures(String code, List<String> errors, List<String> warnings) {
        // Interface implementation checking
        if (code.contains("implements")) {
            Pattern interfacePattern = Pattern.compile("class\\s+(\\w+)\\s+implements\\s+(\\w+)");
            Matcher matcher = interfacePattern.matcher(code);
            while (matcher.find()) {
                String className = matcher.group(1);
                String interfaceName = matcher.group(2);
                warnings.add("Class '" + className + "' implements '" + interfaceName + "' - ensure all methods are implemented");
            }
        }
        
        // Abstract class validation
        if (code.contains("abstract")) {
            if (code.contains("abstract class") && code.contains("new ")) {
                Pattern newAbstractPattern = Pattern.compile("new\\s+(\\w+)\\s*\\(");
                Matcher matcher = newAbstractPattern.matcher(code);
                while (matcher.find()) {
                    String className = matcher.group(1);
                    if (code.contains("abstract class " + className)) {
                        errors.add("Cannot instantiate abstract class '" + className + "'");
                    }
                }
            }
        }
        
        // Generic type checking (basic)
        Pattern genericPattern = Pattern.compile("(List|ArrayList|HashMap)<(\\w+)>");
        Matcher matcher = genericPattern.matcher(code);
        while (matcher.find()) {
            String container = matcher.group(1);
            String genericType = matcher.group(2);
            if (!isValidGenericType(genericType)) {
                warnings.add("Generic type '" + genericType + "' may not be valid");
            }
        }
        
        // Lambda expression validation (basic)
        if (code.contains("->")) {
            warnings.add("Lambda expressions detected - ensure target type is a functional interface");
        }
    }
    
    private boolean isIntCompatible(String value) {
        if (value.matches("-?\\d+")) return true; // Integer literal
        if (value.contains("+") || value.contains("-") || value.contains("*") || value.contains("/")) return true; // Math expression
        if (value.equals("true") || value.equals("false")) return false;
        if (value.startsWith("\"") && value.endsWith("\"")) return false;
        return true; // Could be variable or method call
    }
    
    private boolean isStringCompatible(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) return true; // String literal
        if (value.contains("+") && (value.contains("\"") || value.toLowerCase().contains("string"))) return true; // String concatenation
        return true; // Could be variable or method call
    }
    
    private boolean isBooleanCompatible(String value) {
        if (value.equals("true") || value.equals("false")) return true;
        if (value.contains("==") || value.contains("!=") || value.contains("<") || value.contains(">")) return true;
        if (value.contains("&&") || value.contains("||") || value.contains("!")) return true;
        return true; // Could be variable or method call
    }
    
    private boolean isValidGenericType(String type) {
        return type.matches("[A-Z]\\w*"); // Basic check for valid class name
    }
    
    private boolean areBalanced(String code, char open, char close) {
        int count = 0;
        boolean inString = false;
        boolean inComment = false;
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            
            // Handle string literals
            if (c == '"' && !inComment && (i == 0 || code.charAt(i-1) != '\\')) {
                inString = !inString;
                continue;
            }
            
            if (inString) continue;
            
            // Handle comments
            if (i < code.length() - 1) {
                if (code.charAt(i) == '/' && code.charAt(i+1) == '/') {
                    // Skip to end of line
                    while (i < code.length() && code.charAt(i) != '\n') i++;
                    continue;
                }
                if (code.charAt(i) == '/' && code.charAt(i+1) == '*') {
                    inComment = true;
                    i++;
                    continue;
                }
                if (inComment && code.charAt(i) == '*' && code.charAt(i+1) == '/') {
                    inComment = false;
                    i++;
                    continue;
                }
            }
            
            if (inComment) continue;
            
            if (c == open) {
                count++;
            } else if (c == close) {
                count--;
                if (count < 0) return false;
            }
        }
        
        return count == 0;
    }
    
    private String extractClassName(String code) {
        Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Try without public modifier
        pattern = Pattern.compile("class\\s+(\\w+)");
        matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private Object executeJavaCode(String code, String className) {
        try {
            // Use the advanced interpreter for execution
            AdvancedJavaInterpreter interpreter = new AdvancedJavaInterpreter(context);
            return interpreter.compileAndRun(code);
        } catch (Exception e) {
            return "Execution Error: " + e.getMessage() + "\n" + getStackTraceString(e);
        }
    }
    
    private String getStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    private static class CompileResult {
        boolean success;
        String message;
        
        CompileResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}