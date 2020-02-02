class Lista extends React.Component {
  render() {
    return (
<div class="container">
  <div class="row">{this.props.ekran}</div>
  <div class="row"><a href="#app" class="btn btn-primary" onClick={this.props.powroc}>Powrót</a></div>
</div>
    );
  }
}