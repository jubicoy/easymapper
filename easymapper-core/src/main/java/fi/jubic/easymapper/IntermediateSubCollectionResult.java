package fi.jubic.easymapper;

class IntermediateSubCollectionResult<U, I> {
    private U intermediateRoot;
    private final I subCollection;

    IntermediateSubCollectionResult(
            U intermediateRoot,
            I subCollection
    ) {
        this.intermediateRoot = intermediateRoot;
        this.subCollection = subCollection;
    }

    U getIntermediateRoot() {
        return intermediateRoot;
    }

    void setIntermediateRoot(U intermediateRoot) {
        this.intermediateRoot = intermediateRoot;
    }

    I getSubCollection() {
        return subCollection;
    }
}
