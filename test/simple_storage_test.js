contract('SimpleStorageTest', function(accounts) {
  it("should assert true", function(done) {
    var simple_storage_test = SimpleStorageTest.deployed();
    assert.isTrue(true);
    done();
  });
});
