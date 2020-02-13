const Component=React.Component;
const Link=ReactRouterDOM.Link;

class Card extends Component {
  constructor(props) {
    super(props);
    
    this.state = {
      data: null
    }
  }
  handleClick = () => {
    if (this.props.source.startsWith("http")) {
      window.location = this.props.source;
    }
    else {
      let self = this;
      let kraj = new URLSearchParams(new URL(window.location).search).get("lang");
      if (null == kraj) kraj = navigator.language;
      if (kraj.startsWith("pl")) kraj = "pl";
      axios.post(this.props.source + "?lang=" + kraj, { lang: 'test' }).then(function (response) {
        self.props.replace(<List properties={["produkt", "sklep", "cena", "dodano"]} list={response.data} expandable={true} replace={self.props.replace} back={self.props.back} />);
      })
    }
  }
  render() {
    return (
<div class="card m-3">
  <img src={this.props.image} class="card-img-top" alt="..." />
  <div class="card-body">
    <h5 class="card-title">{this.props.title}</h5>
    <p class="card-text">{this.state.data == null ? this.props.description : this.state.data}</p>
    <a href="#app" class="btn btn-primary" onClick={this.handleClick}>{localise.zobacz}</a>
  </div>
</div>
    )
  }
}

/*

<a><Link to="/listaZakupow">test</Link></a>

*/
