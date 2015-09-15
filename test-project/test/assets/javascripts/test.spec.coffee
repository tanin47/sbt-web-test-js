describe("Test from Coffeescript", () ->
  it("check the value from lib.coffee", () ->
    expect(window.someValueFromLibCoffee).toBe('yes')
  )
)