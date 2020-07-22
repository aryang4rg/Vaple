function createElement(tagName, args = {}, children = []){
	const isSvg = {
		svg: true,
		path: true,
		use: true,
		circle: true,
		defs: true,
		linearGradient: true,
		mask: true,
		g: true,
		stop: true,
		rect: true
	};

	if(isSvg[tagName])
		args.namespace = 'http://www.w3.org/2000/svg';
	var el = args.namespace ? document.createElementNS(args.namespace, tagName) : document.createElement(tagName);

	if(args.className)
		if(args.namespace)
			el.className.baseVal = args.className;
		else
			el.className = args.className;
	if(args.id)
		el.id = args.id;
	if(args.css)
		for(var property in args.css)
			el.style[property] = args.css[property];
	if(args.attributes)
		for(var property in args.attributes)
			el.setAttribute(property, args.attributes[property]);
	if(args.innerText)
		el.appendChild(document.createTextNode(args.innerText));
	el.on = el.addEventListener;

	for(var i = 0; i < children.length; i++)
		el.appendChild(children[i]);
	return el;
}

const ripplingManager = new (class{
	constructor(){
		this.currentRippling = [];

		document.addEventListener('mouseup', () => {
			while(this.currentRippling.length)
				this.clearRipples(this.currentRippling.shift());
		});

		document.addEventListener('touchend', (e) => {
			if(!e.touches.length)
				while(this.currentRippling.length)
					this.clearRipples(this.currentRippling.shift());
		});
	}

	offset(el){
		var box = el.getBoundingClientRect();
		var winOff = {x: window.pageXOffset - document.documentElement.clientLeft, y: window.pageYOffset - document.documentElement.clientTop};

		return {x: box.x + winOff.x, y: box.y + winOff.y,
			top: box.top + winOff.y, bottom: box.bottom + winOff.y,
			left: box.left + winOff.x, right: box.right + winOff.x,
			width: box.width, height: box.height};
	}

	initElement(el, color){
		el.classList.add('waves');
		el.rippling = {color: color, waveContainer: createElement('div', {className: 'wave-container'}), ripples: []};
		el.on('mousedown', (e) => {
			if(e.button == 2)
				return;
			this.addRipple(el, e);
		});

		el.on('touchstart', (e) => {
			this.addRipple(el, e);
		});

		el.appendChild(el.rippling.waveContainer);
	}

	clearRipples(el){
		var ripples = el.rippling.ripples;

		el.rippling.ripples = [];

		for(var i = 0; i < ripples.length; i++)
			this.hideRipple(ripples[i]);
	}

	hideRipple(ripple){
		ripple.style.opacity = 0;

		setTimeout(function(){
			ripple.remove();
		}, 300);
	}

	addRipple(el, e){
		const d = function(x1, x2, y2, y1){
			return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
		};

		const max = function(a){
			var m = a[0];

			for(var i = 1; i < a.length; i++)
				if(a[i] > m)
					m = a[i];
			return m;
		};

		var box = this.offset(el);
		var ripple = createElement('div', {className: 'ripple'});

		const pageX = e.pageX || e.touches[0].pageX;
		const pageY = e.pageY || e.touches[0].pageY;

		ripple.style.left = pageX - box.left + 'px';
		ripple.style.top = pageY - box.top + 'px';
		ripple.style.backgroundColor = el.rippling.color;
		ripple.hide = () => {
			this.hideRipple(this);
		};

		el.rippling.ripples.push(ripple);
		el.rippling.waveContainer.appendChild(ripple);;

		var x1 = box.left;
		var x2 = box.left + box.width;
		var y1 = box.top;
		var y2 = box.top + box.height;

		var w = Math.ceil(2 * max([d(x1, pageX, y1, pageY), d(x2, pageX, y1, pageY), d(x2, pageX, y2, pageY), d(x1, pageX, y2, pageY)]));

		ripple.offsetHeight;
		ripple.style.transform = 'scale(' + w + ')';

		this.currentRippling.push(el);
	};
});

class Page{
	constructor(){
		this.element = createElement('div', {className: 'page'});
	}

	load(data){

	}
}

class ErrorPage extends Page{
	constructor(){
		super();

		this.element.appendChild(createElement('div', {className: 'center'}, [
			createElement('span', {className: 'text', innerText: 'Something went wrong, try again later :('})
		]));
	}
}

class LoginPage extends Page{
	constructor(){
		super();
	}
}

class ProfilePage extends Page{
	constructor(){
		super();

		this.table = createElement('div', {className: 'profile-table'});
		this.left = createElement('div', {className: 'profile-container left'});
		this.middle = createElement('div', {className: 'profile-container middle'});
		this.right = createElement('div', {className: 'profile-container right'});

		this.table.appendChild(this.left);
		this.table.appendChild(createElement('div', {className: 'profile-container expander'}));
		this.table.appendChild(this.middle);
		this.table.appendChild(createElement('div', {className: 'profile-container expander'}));
		this.table.appendChild(this.right);
		this.element.appendChild(this.table);
	}

	load(data){
		console.log(data);
	}
}

class FeedPage extends Page{

}

function generateGradient(svg, ids, stops, maskch = []){
	const gradient = createElement('linearGradient', {id: ids.gradient, attributes: {"x1": "0%", "y1": "0%", "x2": "100%", "y2": "100%"}});

	for(var i = 0; i < stops.length; i++)
		gradient.appendChild(createElement('stop', {attributes: {offset: stops[i].offset, 'stop-color': stops[i].color}}));
	svg.appendChild(createElement('defs', {}, [gradient]));
	svg.appendChild(createElement('mask', {id: ids.mask}, maskch));
	svg.appendChild(createElement('g', {css: {mask: 'url(#' + ids.mask + ')'}}, [
		createElement('rect', {attributes: {x: '-10%', y: '-10%', width: '120%', height: '120%', fill: 'url(#' + ids.gradient + ')'}})
	]));
}

const pageManager = new (class{
	constructor(){
		this.pages = {
			error: new ErrorPage(),
			login: new LoginPage(),
			profile: new ProfilePage(),
			feed: new FeedPage()
		};

		this.showingPage = null;
		this.body = createElement('div', {className: 'page-container'});
		this.topBar = new (class{
			constructor(){
				this.element = createElement('div', {className: 'top-bar'});
				this.logo = createElement('svg', {className: 'logo-container', css: {cursor: 'pointer'}});
				this.logoWaves = createElement('div', {className: 'logo'}, [this.logo]);
				this.element.appendChild(this.logoWaves);
				this.logo.on('click', (e) => {
					e.preventDefault();
					pageLoader.navigate('/');

					return false;
				});

				this.element.appendChild(this.createItem('Dashboard', '/dashboard'));
				this.element.appendChild(this.createItem('Explore', '/explore'));
				this.element.appendChild(this.createItem('Challenges', '/challenges'));

				ripplingManager.initElement(this.logoWaves, 'rgba(0, 0, 0, 0.05)');

				generateGradient(this.logo, {gradient: 'logo-gradient', mask: 'logo-mask'}, [
					{
						offset: "0%",
						color: "#f00"
					},
					{
						offset: "100%",
						color: "#00f"
					}
				], [
					createElement('path', {attributes: {'d': 'M 0,0 15,40 25,40 40,0 30,0 20,28 10,0 0,0z M 35,40 50,0 60,0 75,40 65,40 55,12 45,40 z M 80,0 80,40 90,40 90,25 95,25 A12.5,12.5 0 1,0 95,0 M 95,0 80,0z M 113,0 113,40 143,40 143,30 123,30 123,0 113,0z M 148,0 148,40 178,40 178,30 158,30 158,25 178,25 178,15 158,15 158,10 178,10 178,0 148,0 z'}})
				]);
			}

			createItem(text, path){
				const el = createElement('a', {className: 'quick-nav text', innerText: text});

				el.setAttribute('href', path);
				el.on('click', (e) => {
					e.preventDefault();
					pageLoader.navigate(path);

					return false;
				});

				return el;
			}
		});

		this.body.appendChild(this.topBar.element);

		document.body.appendChild(this.body);
	}

	showPage(page){
		if(this.showingPage != page){
			if(this.showingPage)
				this.body.removeChild(this.showingPage.element);
			this.showingPage = page;
			this.body.appendChild(page.element);
		}
	}

	showErrorPage(){
		this.showPage(this.pages.error);
	}

	load(data){
		if(!data.type)
			return this.showErrorPage();
		const page = this.pages[data.type];

		if(!page)
			return this.showErrorPage();
		page.load(data.data);

		this.showPage(page);
	}
});

class Toast{
	constructor(text){
		this.element = createElement('div', {className: 'toast'});
		this.content = createElement('div', {className: 'toast-content'});
		this.element.appendChild(this.content);

		if(text)
			this.content.appendChild(createElement('span', {className: 'text', innerText: text}));
		this.element.offsetHeight;
	}

	show(){
		this.element.classList.add('show');
	}

	hide(){
		this.element.classList.remove('show');
	}
}

class LoadingToast extends Toast{
	constructor(text){
		super(text);

		this.bar = null;
	}

	indeterminate(){
		if(this.bar)
			this.content.removeChild(this.bar);
		this.bar = createElement('div', {className: 'toast-indeterminate'}, [
			createElement('div', {className: 'waiting-animation'})
		]);

		this.content.appendChild(this.bar);
	}

	progress(){

	}
}

const toastManager = new (class{
	constructor(){
		this.element = createElement('div', {className: 'toast-container'});

		document.body.appendChild(this.element);
	}

	addToast(toast){
		this.element.appendChild(toast.element);
		this.element.offsetHeight;
	}

	removeToast(toast){
		this.element.removeChild(toast.element);
	}
});

const pageLoader = new (class{
	constructor(){
		this.loading = null;
		this.loadingToast = new LoadingToast("Navigating page");
		this.loadingToast.indeterminate();

		toastManager.addToast(this.loadingToast);

		window.addEventListener('popstate', (e) => {
			this.load(location.href);
		});
	}

	navigate(path){
		history.pushState(null, '', path);

		this.load(path);
	}

	load(url){
		if(this.loading && this.loading.readyState < 4)
			this.loading.abort();
		const l = new XMLHttpRequest();

		l.open("GET", url);
		l.setRequestHeader("mode", "ajax");
		l.send();

		l.addEventListener('load', () => {
			this.loadingToast.hide();

			if(l.status != 200)
				return pageManager.showErrorPage();
			var data;

			try{
				data = JSON.parse(l.response);
			}catch(e){
				return pageManager.showErrorPage();
			}

			pageManager.load(data);
		});

		l.addEventListener('error', () => {
			this.loadingToast.hide();
			pageManager.showErrorPage();
		});

		this.loading = l;
		this.loadingToast.show();
	}
});

window.vaple = {pageManager, pageLoader, toastManager};