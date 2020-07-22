class initialLoader extends Page{
	constructor(){
		super();

		this.svg = createElement('svg', {className: 'initial-loader'});

		generateGradient(this.svg, {gradient: 'spinning-gradient', mask: 'spinning-mask'}, [
			{
				offset: "0%",
				color: "#f00"
			},
			{
				offset: "100%",
				color: "#00f"
			}
		], [
			createElement('circle', {attributes: {cx: 50, cy: 50, r: 32}})
		]);

		this.element.appendChild(createElement('div', {className: 'center'}, [this.svg]));
	}
}

(function(){
	vaple.pageManager.showPage(new initialLoader());
	vaple.pageLoader.load(location.href);
})();