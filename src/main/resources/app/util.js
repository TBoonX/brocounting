'use strict';

function lsGet(name) {
    if (localStorage === undefined)
        return null;
    
    var ret = localStorage.getItem(name);
    if (ret === undefined || ret === null || ret.length == 0)
        return null;
}

function lsSet(name, value) {
    if (localStorage === undefined)
        return false;
    
    localStorage.setItem(name, value);
    return true;
}