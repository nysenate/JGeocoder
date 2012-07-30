package net.sourceforge.jgeocoder.us

class AliasResolverTest extends GroovyTestCase {
  void testStateCorrection() {
    assertEquals('PHILADELPHIA', AliasResolver.resolveCityAlias('PHILA', 'PA'))
    assertEquals('PHILADELPHIA', AliasResolver.resolveCityAlias('MANAYUNK', 'PA'))
    assertEquals('CHENANGO BRIDGE', AliasResolver.resolveCityAlias('CHENANGO BRG', 'NY'))
  }
}