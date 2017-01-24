package edu.sjsu.fwjs;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private Map<String,Value> env = new HashMap<String,Value>();
    private Environment outerEnv;

    /**
     * Constructor for global environment
     */
    public Environment() {}

    /**
     * Constructor for local environment of a function
     */
    public Environment(Environment outerEnv) {
        this.outerEnv = outerEnv;
    }

    /**
     * Handles the logic of resolving a variable.
     * If the variable name is in the current scope, it is returned.
     * Otherwise, search for the variable in the outer scope.
     * If we are at the outermost scope (AKA the global scope)
     * null is returned (similar to how JS returns undefined.
     */
    public Value resolveVar(String varName) {
        // YOUR CODE HERE
        Value res = env.get(varName);       //find value in the current env
        if(res == null){
            
            if(outerEnv == null) return null;
            //look at outer env
            return outerEnv.resolveVar(varName);
        }
        return res;
    }

    /**
     * Used for updating existing variables.
     * If a variable has not been defined previously in the current scope,
     * or any of the function's outer scopes, the var is stored in the global scope.
     */
    public void updateVar(String key, Value v) {
        // YOUR CODE HERE
        if(env.containsKey(key)) {  //found key in current scope
            //update val
            env.put(key, v);
        } else if(outerEnv == null) {        //this is currently the global scope
            env.put(key, v);
        } else {                            //ask the outerscope
            outerEnv.updateVar(key, v);
        }
    }

    /**
     * Creates a new variable in the local scope.
     * If the variable has been defined in the current scope previously,
     * a RuntimeException is thrown.
     */
    public void createVar(String key, Value v) {
        // YOUR CODE HERE
        if(env.containsKey(key)){
            throw new RuntimeException("variable name exist already");
        }
        
        env.put(key, v);
    }
}
