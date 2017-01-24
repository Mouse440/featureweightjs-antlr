package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.fwjs.parser.FeatherweightJavaScriptBaseVisitor;
import edu.sjsu.fwjs.parser.FeatherweightJavaScriptParser;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpressionBuilderVisitor extends FeatherweightJavaScriptBaseVisitor<Expression>{
    @Override
    public Expression visitProg(FeatherweightJavaScriptParser.ProgContext ctx) {
        // System.out.println(ctx.getClass());
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i=0; i<ctx.stat().size(); i++) {
            Expression exp = visit(ctx.stat(i));
            if (exp != null) stmts.add(exp);
        }
        return listToSeqExp(stmts);
    }

    @Override
    public Expression visitBareExpr(FeatherweightJavaScriptParser.BareExprContext ctx) {
        // System.out.println(ctx.getClass());
        return visit(ctx.expr());
    }

    @Override
    public Expression visitIfThenElse(FeatherweightJavaScriptParser.IfThenElseContext ctx) {
        // System.out.println(ctx.getClass());
        Expression cond = visit(ctx.expr());
        Expression thn = visit(ctx.block(0));
        Expression els = visit(ctx.block(1));
        return new IfExpr(cond, thn, els);
    }

    @Override
    public Expression visitIfThen(FeatherweightJavaScriptParser.IfThenContext ctx) {
        // System.out.println(ctx.getClass());
        Expression cond = visit(ctx.expr());
        Expression thn = visit(ctx.block());
        return new IfExpr(cond, thn, null);
    }
    
    @Override
    public Expression visitWhileStat(FeatherweightJavaScriptParser.WhileStatContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        return new WhileExpr(visit(ctx.expr()), visit(ctx.block()));
    }
    
    @Override
    public Expression visitPrintStat(FeatherweightJavaScriptParser.PrintStatContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        return new PrintExpr(visit( ctx.expr() ));
    }
    
    @Override
    public Expression visitEmptyStat(FeatherweightJavaScriptParser.EmptyStatContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass() + " not implemeted yet");
        return null;
    }
    
    @Override
    public Expression visitFunctionDecl(FeatherweightJavaScriptParser.FunctionDeclContext ctx) {

        // System.out.println(ctx.getClass());
        ParamsExpr paramExp = (ParamsExpr)visit(ctx.params());
        ParamsVal paramVals = (ParamsVal)paramExp.evaluate(null); //get parameters
        
        List<Expression> exprList = new ArrayList();
        for(int i = 0; i < ctx.stat().size(); i++) {
            exprList.add( visit( ctx.stat().get(i) ) ); // get the expressions from the statements within this ctx
        }
        
        return new FunctionDeclExpr(paramVals.toParams(), listToSeqExp(exprList));
    }
    
    @Override
    public Expression visitParameters(FeatherweightJavaScriptParser.ParametersContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        if(ctx.ID().isEmpty()) {    //return 
            return new ParamsExpr(new ArrayList<String>());
        } else {
            List<String> paramList = new ArrayList();
            for(int i = 0; i < ctx.ID().size(); i++) {
                paramList.add( ctx.ID().get(i).getText() ); 
            }
            
            return new ParamsExpr(paramList);
        }
    }
    
    @Override
    public Expression visitAssign(FeatherweightJavaScriptParser.AssignContext ctx) {
        //Need to be implemented
        Expression e = visit(ctx.expr());
        return new AssignExpr(ctx.ID().getText(), e);
    }
    
    @Override
    public Expression visitMulDivMod(FeatherweightJavaScriptParser.MulDivModContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            Expression exp = visit(ctx.expr(i));
            stmts.add(exp);
        }
        
        return new BinOpExpr(getOp(ctx.op.getText()), stmts.get(0), stmts.get(1));
    }
    
    @Override
    public Expression visitFunctionAppl(FeatherweightJavaScriptParser.FunctionApplContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        ArgsExpr argsExpr = (ArgsExpr)visit(ctx.args());
        ArgsVal argsVal = (ArgsVal)argsExpr.evaluate(null);
        
        return new FunctionAppExpr(visit(ctx.expr()), argsVal.toArgs() );
    }
    
    @Override
    public Expression visitConstant(FeatherweightJavaScriptParser.ConstantContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        if(ctx.BOOL() != null) {
            boolean bool = Boolean.parseBoolean( ctx.BOOL().getText() );
            return new ValueExpr(new BoolVal(bool));
            
        } else if (ctx.INT() != null) {
            int val = Integer.valueOf( ctx.INT().getText() );
            return new ValueExpr(new IntVal(val) );

        } else if (ctx.NULL() != null) {
            return new ValueExpr(new NullVal() );
        } else {
            return null;
        }
    }
    
    @Override
    public Expression visitAddSub(FeatherweightJavaScriptParser.AddSubContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
            
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            Expression exp = visit(ctx.expr(i));
            stmts.add(exp);
        }
        return new BinOpExpr(getOp( ctx.op.getText() ), stmts.get(0), stmts.get(1));
    }
    
    /**
     * This function is assuming that ctx.id needs to be resolved
     */
    @Override
    public Expression visitVarAppl(FeatherweightJavaScriptParser.VarApplContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        return new VarExpr(ctx.ID().getText());
    }
 
    
    @Override
    public Expression visitCompare(FeatherweightJavaScriptParser.CompareContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            Expression exp = visit(ctx.expr(i));
            stmts.add(exp);
        }
        
        return new BinOpExpr(getOp(ctx.op.getText()),stmts.get(0), stmts.get(1));
    }
    
    @Override
    public Expression visitVarDecl(FeatherweightJavaScriptParser.VarDeclContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        String variableId = ctx.ID().getText();
        Expression variableVal = visit(ctx.expr());     //get expression by visiting ctx.expr()
        return new VarDeclExpr(variableId, variableVal);
    }
    
    
    
    @Override
    public Expression visitInt(FeatherweightJavaScriptParser.IntContext ctx) {
        // System.out.println(ctx.getClass());
        int val = Integer.valueOf(ctx.INT().getText());
        return new ValueExpr(new IntVal(val));
    }
    
    @Override
    public Expression visitArguments(FeatherweightJavaScriptParser.ArgumentsContext ctx) {
        //Need to be implemented
        // System.out.println(ctx.getClass());
        
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i = 0; i < ctx.expr().size(); i++) {
            Expression exp = visit(ctx.expr(i));
            stmts.add(exp);
        }
        return new ArgsExpr(stmts);
    }
    
    
    
    @Override
    public Expression visitParens(FeatherweightJavaScriptParser.ParensContext ctx) {
        // System.out.println(ctx.getClass());
        return visit(ctx.expr());
    }

    @Override
    public Expression visitFullBlock(FeatherweightJavaScriptParser.FullBlockContext ctx) {
        // System.out.println(ctx.getClass());
        List<Expression> stmts = new ArrayList<Expression>();
        for (int i=1; i<ctx.getChildCount()-1; i++) {
            Expression exp = visit(ctx.getChild(i));
            stmts.add(exp);
        }
        return listToSeqExp(stmts);
    }
    
    @Override
    public Expression visitSimpBlock(FeatherweightJavaScriptParser.SimpBlockContext ctx) {
        // System.out.println(ctx.getClass());
        return visit(ctx.stat());
    }
    
    /**
     * Converts a list of expressions to one sequence expression,
     * if the list contained more than one expression.
     */
    private Expression listToSeqExp(List<Expression> stmts) {
        if (stmts.isEmpty()) return null;
        Expression exp = stmts.get(0);
        for (int i=1; i<stmts.size(); i++) {
            exp = new SeqExpr(exp, stmts.get(i));
        }
        return exp;
    }
    
    /**
     * Get Op object from input string
     */
    private Op getOp(String opSymbol) {
        if("+".equals(opSymbol)) {
            return Op.ADD;
        } else if ("-".equals(opSymbol)) {
            return Op.SUBTRACT;
        } else if ("*".equals(opSymbol)) {
            return Op.MULTIPLY;
        } else if ("/".equals(opSymbol)) {
            return Op.DIVIDE;
        } else if ("%".equals(opSymbol)) {
            return Op.MOD;
        } else if (">".equals(opSymbol) ) {
            return Op.GT;
        } else if (">=".equals(opSymbol) ) {
            return Op.GE;
        } else if ("<".equals(opSymbol) ) {
            return Op.LT;
        } else if ("<=".equals(opSymbol) ) {
            return Op.LE;
        } else if ("==".equals(opSymbol) ) {
            return Op.EQ;
        }  else {
            return null;
        }
    }
}