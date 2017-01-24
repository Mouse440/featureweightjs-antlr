package edu.sjsu.fwjs;

import java.util.ArrayList;
import java.util.List;

/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;
    public ValueExpr(Value v) {
        this.val = v;
    }
    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;
    public VarExpr(String varName) {
        this.varName = varName;
    }
    public Value evaluate(Environment env) {
        Value var = env.resolveVar(varName);
        if (var == null) {
            return new NullVal();
        } else {
            return var;
        }
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;
    public PrintExpr(Expression exp) {
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value v = exp.evaluate(env);
        System.out.println(v.toString());
        return v;
    }
}
/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;
    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        Value v1 = this.e1.evaluate(env);
        Value v2 = this.e2.evaluate(env);

        // YOUR CODE HERE
        if(this.op == Op.ADD) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new IntVal(iv1.toInt() + iv2.toInt());
        } else if (this.op == op.SUBTRACT) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new IntVal(iv1.toInt() - iv2.toInt());
        } else if (this.op == op.MULTIPLY) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new IntVal(iv1.toInt() * iv2.toInt());
        } else if (this.op == op.DIVIDE) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new IntVal(iv1.toInt() / iv2.toInt());
        } else if (this.op == op.MOD) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new IntVal(iv1.toInt() % iv2.toInt());
        } else if (this.op == op.GT) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new BoolVal(iv1.toInt() > iv2.toInt());
        } else if (this.op == op.GE) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new BoolVal(iv1.toInt() >= iv2.toInt());
        } else if (this.op == op.LT) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new BoolVal(iv1.toInt() < iv2.toInt());
        } else if (this.op == op.LE) {
            IntVal iv1 = (IntVal) v1;
            IntVal iv2 = (IntVal) v2;
            return new BoolVal(iv1.toInt() <= iv2.toInt());
        } else if (this.op == op.EQ) {
            return new BoolVal(v1.equals(v2) && v2.equals(v1));
        } else {
            return null;
        }
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;
    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }
    public Value evaluate(Environment env) {
        Value cond = this.cond.evaluate(env);
        
        if( cond.equals(new BoolVal(true)) ) {
            return this.thn.evaluate(env);
        } else if ( cond.equals(new BoolVal(false)) ) {
            if(this.els != null) {
                Value val = this.els.evaluate(env);
                return val;
            }
            return null;
        } else {
            throw new RuntimeException("Only booleans accepted in If statements!");
        }
    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;
    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        while(this.cond.evaluate(env).equals(new BoolVal(true))) {
            this.body.evaluate(env);
        };
        
        return null;
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;
    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        e1.evaluate(env);
        Value v2 = e2.evaluate(env);
        return v2;
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;
    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        Value v = exp.evaluate(env);
        env.createVar(varName, v);
        return env.resolveVar(varName);
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;
    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }
    public Value evaluate(Environment env) {
//        System.out.println("var name is " + varName);
        Value val = e.evaluate(env);
//        System.out.println("e is " + val.toString());

        env.updateVar(varName,val );
        return env.resolveVar(varName);
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;
    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        ClosureVal closure = new ClosureVal(params, body, env);
        return closure;
    }
}

/**
 * Function application.
 */
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;
    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f;
        this.args = args;
    }
    public Value evaluate(Environment env) {
        // YOUR CODE HERE

        ArrayList<Value> evaluatedArgs = new ArrayList<Value>();

        for (Expression arg : args) {
            evaluatedArgs.add(arg.evaluate(env));
        }

        ClosureVal closure = (ClosureVal)f.evaluate(env);
        return closure.apply(evaluatedArgs);
    }
}

/**
 * A params declaration, which eva.
 */
class ParamsExpr implements Expression {
    private List<String> params;
    public ParamsExpr(List<String> params) {
        this.params = params;
    }
    @Override
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        ParamsVal paramsVal = new ParamsVal(params);
        return paramsVal;
    }

}

/**
 * A params declaration, which eva.
 */
class ArgsExpr implements Expression {
    private List<Expression> args;
    public ArgsExpr(List<Expression> args) {
        this.args = args;
    }
    @Override
    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        ArgsVal argsVal = new ArgsVal(this.args);
        return argsVal;
    }

}
