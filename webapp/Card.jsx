class Card extends React.Component {
  constructor(props) {
    super(props);
    
    this.state = {
      data: null
    }
  }
  handleClick = () => {
    if (this.props.zrodlo.startsWith("http")) {
      window.location = this.props.zrodlo;
    }
    else {
      var self = this;
      axios.get(this.props.zrodlo).then(function (response) {
        self.props.aktualizuj(<Lista ekran={JSON.stringify(response.data)} powroc={self.props.powroc} />);
      })
    }
  }
  render() {
    return (
<div class="card">
  <img src={this.props.ikona} class="card-img-top" alt="..." />
  <div class="card-body">
    <h5 class="card-title">{this.props.tytul}</h5>
    <p class="card-text">{this.state.data == null ? this.props.opis : this.state.data}</p>
    <a href="#app" class="btn btn-primary" onClick={this.handleClick}>Zobacz</a>
  </div>
</div>
    )
  }
}