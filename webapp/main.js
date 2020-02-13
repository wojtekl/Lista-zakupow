const Router=ReactRouterDOM.HashRouter;
const Route=ReactRouterDOM.Route;

ReactDOM.render(<App />, document.getElementById('app'));

/*

(
<Router>
  <Route exact path="/" component={App} />
  <Route path="/listaZakupow" render={ () => <List properties={["produkt", "sklep", "cena", "dodano"]} list={[]} />} />
</Router>
)

*/
