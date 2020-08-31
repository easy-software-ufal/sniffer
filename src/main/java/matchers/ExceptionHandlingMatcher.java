package matchers;

import com.github.javaparser.ast.Node;
import utils.OutputWriter;
import utils.TestClass;
import utils.TestMethod;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ExceptionHandlingMatcher extends SmellMatcher {

    @Override
    protected void match(TestClass testClass) {
        for (TestMethod testMethod : testClass.getTestMethods()) {
            for (Node node : testMethod.getMethodDeclaration().getChildNodes()) {
                List<Integer> lines = new LinkedList<>();
                if (matchExceptionHandling(node.getChildNodes(), lines)) {
                    write(testMethod.getTestFilePath(), "Exception Handling", testMethod.getMethodDeclaration().getNameAsString(), lines.toString());
                    try {
                        OutputWriter.csvWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void write(String filePath, String testSmell, String methodName, String lines) {
        OutputWriter.getInstance().write(filePath, testSmell, methodName, lines);
        Logger.getLogger(AssertionRouletteMatcher.class.getName()).info("Found exception handling in method \"" + methodName + "\" in lines " + lines);
    }

    private boolean matchExceptionHandling(List<Node> nodeList, List<Integer> lines) {
        if (nodeList.size() > 0) {
            Node lastNode = nodeList.get(nodeList.size() - 1);
            if (lastNode.getMetaModel().getTypeName().equals("TryStmt")) {
                for (Node blockOrCatch : lastNode.getChildNodes()) {
                    if (blockOrCatch.getMetaModel().getTypeName().equals("BlockStmt")) {
                        for (Node node : blockOrCatch.getChildNodes()) {
                            if (node.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                                if (node.toString().startsWith("Assert")
                                        || node.toString().startsWith("assert")
                                        || node.toString().startsWith("fail")) {
                                    lines.add(node.getRange().get().begin.line);
                                    return true;
                                }
                            }
                        }
                    } else if (blockOrCatch.getMetaModel().getTypeName().equals("CatchClause")) {
                        for (Node blockStmt : blockOrCatch.getChildNodes()) {
                            if (blockStmt.getMetaModel().getTypeName().equals("BlockStmt")) {
                                for (Node node : blockStmt.getChildNodes()) {
                                    if (node.getMetaModel().getTypeName().equals("ExpressionStmt")) {
                                        if (node.toString().startsWith("Assert")
                                                || node.toString().startsWith("assert")
                                                || node.toString().startsWith("fail")) {
                                            lines.add(node.getRange().get().begin.line);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}