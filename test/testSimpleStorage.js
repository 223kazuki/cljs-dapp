"use strict";

var SimpleStorage = artifacts.require("SimpleStorage");

contract('SimpleStorage', ([user]) => {
    let storage;

    beforeEach('setup contract for each test', async function () {
        storage = await SimpleStorage.new(123, { from: user });
    })

    it("should be initialized successfully.", async () => {
        let data = await storage.get();
        assert(data.toNumber() === 123, "Initial data is correct.");
    })

    it("should be set data successfully.", async () => {
        await storage.set(124, { from: user });
        let data = await storage.get();
        assert(data.toNumber() === 124, "Data is correct.");
    })
})