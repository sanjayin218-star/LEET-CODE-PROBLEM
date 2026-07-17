var checkIfInstanceOf = function(obj, classFunction) {

    if (obj === null || obj === undefined) {
        return false;
    }

    if (typeof classFunction !== "function") {
        return false;
    }

    return obj instanceof classFunction ||
           obj.constructor === classFunction;
};
