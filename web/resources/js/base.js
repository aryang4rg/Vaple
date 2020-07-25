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
	el.setText = function(text){
		if(this.childNodes.length == 1 && this.childNodes[0].nodeType == document.TEXT_NODE && this.childNodes[0].textContent == text)
			return;
		while(this.childNodes.length)
			this.removeChild(this.childNodes[0]);
		this.appendChild(document.createTextNode(text));
	};

	el.on = el.addEventListener;

	for(var i = 0; i < children.length; i++)
		el.appendChild(children[i]);
	return el;
}

function cdnPath(subdir, name){
	if(subdir)
		return '/cdn/' + subdir + '/' + name + '.png';
	return '/cdn/' + name + '.png';
}

function setBackgroundImage(element, url){
	element.style.backgroundImage = 'url(' + url + ')';
}

function ajaxify(el){
	el.on('click', function(e){
		e.preventDefault();
		pageLoader.load(this.getAttribute('href'));

		return false;
	});
}

window.mapCallbacks = [];
window.initMap = function(){
	for(var i = 0; i < this.mapCallbacks.length; i++)
		this.mapCallbacks[i]();
	this.mapCallbacks = null;
};

window.waitForMap = function(callback){
	if(this.mapCallbacks)
		this.mapCallbacks.push(callback);
	else
		callback();
}

const numberToMonth = [
	'January',
	'February',
	'March',
	'April',
	'May',
	'June',
	'July',
	'August',
	'September',
	'October',
	'November',
	'December'
];

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

class Toast{
	constructor(text){
		this.element = createElement('div', {className: 'toast'});
		this.content = createElement('div', {className: 'toast-content'});
		this.element.appendChild(this.content);
		this.text = createElement('span', {className: 'text', innerText: text ? text : ''});
		this.content.appendChild(this.text);
		this.element.offsetHeight;
		this.timeout = null;
	}

	show(){
		this.element.classList.add('show');

		if(this.timeout){
			clearTimeout(this.timeout);

			this.timeout = null;
		}
	}

	hide(){
		this.element.classList.remove('show');

		if(this.timeout){
			clearTimeout(this.timeout);

			this.timeout = null;
		}
	}

	hideAfter(ms){
		if(this.timeout)
			clearTimeout(this.timeout);
		this.timeout = setTimeout(() => {
			this.hide();
		}, ms);
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
		if(this.bar)
			this.content.removeChild(this.bar);
		this.bar = null;

		/* todo */
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

class Page{
	constructor(){
		this.element = createElement('div', {className: 'page'});
	}

	showing(){}

	hidden(){}

	load(data){}
}

class ErrorPage extends Page{
	constructor(){
		super();

		this.element.appendChild(createElement('div', {className: 'center'}, [
			createElement('span', {className: 'text', innerText: 'Something went wrong, try again later :('})
		]));
	}
}

class NotFoundPage extends Page{
	constructor(){
		super();

		this.element.appendChild(createElement('div', {className: 'center'}, [
			createElement('span', {className: 'text', innerText: "We could not find the page you're looking for :("})
		]));
	}
}

class UnverifiedPage extends Page{
	constructor(){
		super();

		this.element.appendChild(createElement('div', {className: 'center'}, [
			createElement('span', {className: 'text', innerText: "Please check your email for a verification link"})
		]));
	}
}

class LoginPage extends Page{
	constructor(){
		super();

		this.mode = 'login';
		this.showingsuccess = false;
		this.emailIcon = createElement('svg', {className: 'login-form-entry-icon'}, [
			createElement('path', {attributes: {fill: 'none', d: 'M0 0h24v24H0z'}}),
			createElement('path', {attributes: {d: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10h5v-2h-5c-4.34 0-8-3.66-8-8s3.66-8 8-8 8 3.66 8 8v1.43c0 .79-.71 1.57-1.5 1.57s-1.5-.78-1.5-1.57V12c0-2.76-2.24-5-5-5s-5 2.24-5 5 2.24 5 5 5c1.38 0 2.64-.56 3.54-1.47.65.89 1.77 1.47 2.96 1.47 1.97 0 3.5-1.6 3.5-3.57V12c0-5.52-4.48-10-10-10zm0 13c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3z'}})
		]);

		this.passwordIcon = createElement('svg', {className: 'login-form-entry-icon'}, [
			createElement('path', {attributes: {fill: 'none', d: 'M0 0h24v24H0z'}}),
			createElement('path', {attributes: {d: 'M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z'}})
		]);

		this.nameIcon = createElement('svg', {className: 'login-form-entry-icon'}, [
			createElement('path', {attributes: {fill: 'none', d: 'M0 0h24v24H0z'}}),
			createElement('path', {attributes: {d: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z'}})
		]);

		this.form = createElement('div', {className: 'login-form shadow-heavy'});
		this.title = createElement('span', {className: 'login-form-title text metro', innerText: 'Login'});
		this.form.appendChild(this.title);
		this.formContainer = createElement('div', {className: 'login-form-container'});
		this.entries = createElement('div', {className: 'login-form-entries'});
		this.name = this.createEntry('Name', 'text', this.nameIcon);
		this.entries.appendChild(this.name.entry);
		this.email = this.createEntry('Email', 'email', this.emailIcon);
		this.entries.appendChild(this.email.entry);
		this.password = this.createEntry('Password', 'password', this.passwordIcon);
		this.entries.appendChild(this.password.entry);
		this.entries.appendChild(createElement('div', {className: 'login-form-entry break'}));
		this.country = this.createEntry('Country', 'text');
		this.entries.appendChild(this.country.entry);
		this.state = this.createEntry('Province/State', 'text');
		this.entries.appendChild(this.state.entry);
		this.city = this.createEntry('City', 'text');
		this.entries.appendChild(this.city.entry);
		this.formContainer.appendChild(this.entries);
		this.button = createElement('div', {className: 'login-button text metro', innerText: 'login'});
		this.formContainer.appendChild(this.button);
		this.actionError = createElement('span', {className: 'login-form-entry-error text metro'});
		this.formContainer.appendChild(this.actionError);
		this.textcontainer = createElement('div', {className: 'login-change-container'});
		this.text = createElement('span', {className: 'text metro'});
		this.textbutton = createElement('span', {className: 'login-change-button text metro'});
		this.textcontainer.appendChild(this.text);
		this.textcontainer.appendChild(this.textbutton);
		this.formContainer.appendChild(this.textcontainer);
		this.successText = createElement('span', {className: 'text metro'});
		this.form.appendChild(this.formContainer);
		this.element.appendChild(createElement('div', {className: 'center'}, [this.form]));
		this.element.classList.add('login-background');

		this.name.entry.style.display = 'none';
		this.country.entry.style.display = 'none';
		this.state.entry.style.display = 'none';
		this.city.entry.style.display = 'none';
		this.actionError.style.display = 'none';

		this.textbutton.on('click', () => {
			if(!accountManager.needLogin())
				return;
			if(this.mode == 'login')
				this.signupMode();
			else
				this.loginMode();
		});

		this.button.on('click', () => {
			this.trySubmit();
		});
	}

	createEntry(name, type, icon){
		const entry = createElement('div', {className: 'login-form-entry'});
		const cont = createElement('div', {className: 'login-form-entry-input-container'});
		const field = createElement('input', {className: 'login-form-entry-input text metro', attributes: {placeholder: 'Enter your ' + name.toLowerCase(), type, spellcheck: false}});
		const error = createElement('span', {className: 'login-form-entry-error text metro'});

		entry.appendChild(createElement('span', {className: 'text metro', innerText: name}));

		if(icon)
			cont.appendChild(icon);
		cont.appendChild(field);
		cont.appendChild(createElement('div', {className: 'login-form-entry-input-focus-visualizer login-background'}));
		entry.appendChild(cont);
		entry.appendChild(error);
		field.on('keyup', (e) => {
			if(e.keyCode == 13)
				this.trySubmit();
		});

		return {entry, field, error};
	}

	loginMode(){
		this.text.setText("Don't have an account?");
		this.textbutton.setText("Sign up");
		this.button.setText('login');
		this.title.setText('Login');
		this.name.entry.style.display = 'none';
		this.name.entry.style.display = 'none';
		this.country.entry.style.display = 'none';
		this.state.entry.style.display = 'none';
		this.city.entry.style.display = 'none';
		this.form.style.width = '';
		this.actionError.style.display = 'none';
		this.mode = 'login';
	}

	signupMode(){
		this.text.setText("Have an account already?");
		this.textbutton.setText("Log in");
		this.button.setText('Sign Up');
		this.title.setText('Sign up');
		this.name.entry.style.display = '';
		this.name.entry.style.display = '';
		this.country.entry.style.display = '';
		this.state.entry.style.display = '';
		this.city.entry.style.display = '';
		this.form.style.width = '640px';
		this.actionError.style.display = 'none';
		this.mode = 'signup';
	}

	showing(){
		this.email.field.value = '';
		this.password.field.value = '';
		this.name.field.value = '';
		this.country.field.value = '';
		this.state.field.value = '';
		this.city.field.value = '';

		this.loginMode();

		if(this.showingsuccess){
			this.showingsuccess = false;
			this.form.appendChild(this.formContainer);
			this.form.removeChild(this.successText);
		}
	}

	load(){
		document.title = 'Login';
		history.replaceState(null, document.title, '/login');
	}

	finish(token, reason){
		this.button.classList.remove('disabled');

		if(reason || !token){
			this.actionError.style.display = '';

			if(!reason){
				if(this.mode == 'login'){
					if(token === null)
						reason = 'Invalid credentials';
					else
						reason = 'There was an error logging in, try again later';
				}else
					reason = 'There was an error creating your account, try again later';
			}

			this.actionError.setText(reason);
		}else{
			this.form.style.width = '';

			if(!this.showingsuccess){
				this.showingsuccess = true;
				this.form.removeChild(this.formContainer);
				this.form.appendChild(this.successText);
			}

			if(this.mode == 'login')
				this.successText.setText('Login successful. You will be shortly redirected');
			else
				this.successText.setText('Signup successful. You will be shortly redirected');
		}
	}

	trySubmit(){
		if(!accountManager.needLogin())
			return;
		this.actionError.style.display = 'none';

		var error = false;

		if(this.email.field.value)
			this.email.error.setText('');
		else{
			this.email.error.setText('Enter your email');

			error = true;
		}

		if(this.password.field.value){
			if(this.password.field.value.length > 32){
				error = true;

				this.password.error.setText('Password can be atmost 32 characters');
			}else
				this.password.error.setText('');
		}else{
			this.password.error.setText('Enter your password');

			error = true;
		}

		if(this.mode == 'login'){
			if(error)
				return;
			this.button.classList.add('disabled');

			accountManager.login(this.email.field.value, this.password.field.value);
		}else{
			if(this.name.field.value)
				this.name.error.setText('');
			else{
				this.name.error.setText('Enter your name');
				error = true;
			}

			const email = this.email.field.value;
			var pi = email.lastIndexOf('.');
			var ai = email.indexOf('@');

			if(ai < 1 || pi <= ai + 1 || pi + 1 >= email.length){
				this.email.error.setText('Enter a valid email');
				error = true;
			}else{
				this.email.error.setText('');
			}

			if(this.country.field.value)
				this.country.error.setText('');
			else{
				this.country.error.setText('Enter a valid country');
				error = true;
			}

			if(this.state.field.value)
				this.state.error.setText('');
			else{
				this.state.error.setText('Enter a valid state');
				error = true;
			}

			if(this.city.field.value)
				this.city.error.setText('');
			else{
				this.city.error.setText('Enter a valid city');
				error = true;
			}

			if(error)
				return;
			this.button.classList.add('disabled');

			accountManager.signup(this.name.field.value, this.email.field.value, this.password.field.value,
				this.country.field.value, this.state.field.value, this.city.field.value);
		}
	}
}

class ProfilePage extends Page{
	constructor(){
		super();

		this.self = false;
		this.submittingToast = new LoadingToast('Changing');
		this.followFailed = new LoadingToast('Failed to (un)follow the user, try again');

		toastManager.addToast(this.submittingToast);
		toastManager.addToast(this.followFailed);

		this.submitpfp = null;
		this.submittedpfp = null;
		this.id = null;

		this.table = createElement('div', {className: 'profile-table'});
		this.left = createElement('div', {className: 'profile-container left'});
		this.middle = createElement('div', {className: 'profile-container middle'});
		this.right = createElement('div', {className: 'profile-container right'});
		this.left.appendChild(createElement('div', {className: 'profile-container top-padding'}));
		this.right.appendChild(createElement('div', {className: 'profile-container top-padding'}));

		this.table.appendChild(this.left);
		this.table.appendChild(createElement('div', {className: 'profile-container expander'}));
		this.table.appendChild(this.middle);
		this.table.appendChild(createElement('div', {className: 'profile-container expander'}));
		this.table.appendChild(this.right);
		this.element.appendChild(this.table);

		this.profileAboutContainer = createElement('div', {className: 'profile-about-container shadow-light'});
		this.profileImage = createElement('div', {className: 'profile-photo shadow-heavy'});
		this.profilePhotoContainer = createElement('div', {className: 'profile-photo-container'});
		this.profilePhotoContainer.appendChild(this.profileImage);
		this.fileChooser = createElement('input', {attributes: {type: 'file', name: 'name', accept: 'image/*'}});
		this.photoEditButton = createElement('div', {className: 'profile-edit-button text metro', innerText: 'Change avatar'});
		this.profilePhotoContainer.appendChild(this.photoEditButton);
		this.profileAboutContainer.appendChild(this.profilePhotoContainer);
		this.details = createElement('div', {className: 'profile-details'});
		this.profileAboutContainer.appendChild(this.details);
		this.name = this.createEditableString('profile-name text');
		this.details.appendChild(this.name.element);
		this.followcount = createElement('div', {className: 'number text'});
		this.followercount = createElement('div', {className: 'number text'});
		this.details.appendChild(createElement('div', {className: 'profile-follow-count'}, [
			createElement('div', {className: 'profile-follow-counter'}, [
				createElement('div', {className: 'title text', innerText: 'Followers'}),
				this.followercount
			]),
			createElement('div', {className: 'divider'}),
			createElement('div', {className: 'profile-follow-counter'}, [
				createElement('div', {className: 'title text', innerText: 'Following'}),
				this.followcount
			]),
		]));

		this.photoEditButton.on('click', () => {
			this.fileChooser.click();
		});

		this.fileChooser.on('change', (file) => {
			var input = file.target;

			var reader = new FileReader();

			reader.onload = () => {
				this.submitpfp = reader.result;
				this.edited();

				setBackgroundImage(this.profileImage, reader.result);
			};

			reader.readAsDataURL(input.files[0]);
		});

		this.details.appendChild(createElement('div', {className: 'divider'}));
		this.bio = this.createEditableString('profile-bio text', 512, true);
		this.details.appendChild(this.bio.element);
		this.details.appendChild(createElement('div', {className: 'divider'}));
		this.location = createElement('div', {className: 'profile-location'});
		this.details.appendChild(this.location);
		this.country = this.createEditableString('text metro');
		this.location.appendChild(this.country.element);
		this.state = this.createEditableString('text metro');
		this.location.appendChild(this.state.element);
		this.city = this.createEditableString('text metro');
		this.location.appendChild(this.city.element);
		this.submit = createElement('div', {className: 'profile-submit text metro', innerText: 'save', css: {display: 'none'}});
		this.details.appendChild(this.submit);
		this.follow = createElement('div', {className: 'profile-follow text metro', innerText: 'follow', css: {display: 'none'}});
		this.details.appendChild(this.follow);
		this.error = createElement('span', {className: 'profile-submit-error text metro'});
		this.details.appendChild(this.error);
		this.left.appendChild(this.profileAboutContainer);

		this.submit.on('click', () => {
			if(accountManager.submitting)
				return;
			this.error.setText('');
			this.submit.classList.add('disabled');
			this.submittingToast.text.setText('Changing');
			this.submittingToast.show();
			this.submittingToast.indeterminate();
			this.submittedpfp = this.submitpfp;

			accountManager.changeAccount(this.name.input.value, this.bio.input.value, this.country.input.value, this.state.input.value, this.city.input.value, this.submitpfp);

			this.submitpfp = null;
		});

		this.isFollowing = false;
		this.follow.on('click', () => {
			const change = {};

			if(this.isFollowing){
				change.unfollowing = [this.id];

				this.follow.setText('Follow');
				this.isFollowing = false;
			}else{
				change.following = [this.id];

				this.follow.setText('Unfollow');
				this.isFollowing = true;
			}

			if(this.followRequest){
				this.followRequest.abort();
				this.followRequest = null;

				return;
			}

			const x = this.followRequest = accountManager.sendRequest('/follow_change', change, (status, error, resp) => {
				var err = false;

				if(error || status != 200){
					this.followFailed.show();
					this.followFailed.hideAfter(2000);

					err = true;
				}

				if(this.followRequest == x){
					this.followRequest = null;

					if(!err)
						return;
					if(this.isFollowing){
						this.follow.setText('Follow');
						this.isFollowing = false;
					}else{
						this.follow.setText('Unfollow');
						this.isFollowing = true;
					}
				}
			});
		});

		this.clubs = createElement('div', {className: 'profile-clubs shadow-light'});
		this.clubTitleContainer = createElement('div', {className: 'profile-clubs-title-container'});
		this.clubTitleContainer.appendChild(createElement('span', {className: 'profile-clubs-title text metro', innerText: 'Clubs'}));
		this.clubs.appendChild(this.clubTitleContainer);
		this.noneText = createElement('span', {className: 'text metro', css: {display: 'none'}});
		this.clubs.appendChild(this.noneText);
		this.clubsList = createElement('div', {className: 'profile-clubs-list'});
		this.clubs.appendChild(this.clubsList);
		this.right.appendChild(this.clubs);

		this.newClubButton = createElement('a', {className: 'profile-clubs-title-new-container', css: {display: 'none'}, attributes: {href: '/new_club'}});
		this.newClubButtonSVG = createElement('svg', {className: 'profile-clubs-title-new', attributes: {viewBox: '0 0 48 48'}});

		ajaxify(this.newClubButton);
		generateGradient(this.newClubButtonSVG, {gradient: 'activity-add-gradient', mask: 'activity-add-mask'}, [
			{
				offset: "0%",
				color: "#f00"
			},
			{
				offset: "100%",
				color: "#00f"
			}
		], [
			createElement('path', {attributes: {d: 'M23.5,23.5 m -19.5 0 a 19.5,19.5 0 1,0 39,0 a 19.5,19.5 0 1,0 -39,0 z', stroke: '#fff', 'stroke-width': '3px'}}),
			createElement('path', {attributes: {d: 'M14,22 22,22 22,14 25,14 25,22 33,22 33,25 25,25 25,33 22,33 22,25 25,25 14,25z', fill: '#fff'}})
		]);

		this.newClubButton.appendChild(this.newClubButtonSVG);
		this.clubTitleContainer.appendChild(this.newClubButton);

		this.fetch_activities = null;
		this.activity_last = null;
		this.activity_next = false;

		this.svg = createElement('svg', {className: 'activities-loader'});

		generateGradient(this.svg, {gradient: 'activities-gradient', mask: 'activities-mask'}, [
			{
				offset: "0%",
				color: "#0f0"
			},
			{
				offset: "100%",
				color: "#00f"
			}
		], [
			createElement('circle', {attributes: {cx: 50, cy: 50, r: 16}})
		]);

		this.hasCenterLoading = false;
		this.centerLoadingIndicator = createElement('div', {className: 'center'});
		this.hasLoading = false;
		this.loadingIndicator = createElement('div', {className: 'activities-loader-center'});

		this.element.on('scroll', (e) => {
			const min = Math.max(0, this.element.offsetHeight - 250);
			const pscroll = Math.max(0, this.element.scrollTop - Math.max(0,
				this.profileAboutContainer.offsetHeight - min));
			const cscroll = Math.max(0, this.element.scrollTop - Math.max(0,
				this.clubs.offsetHeight - min));
			this.profileAboutContainer.style.transform = 'translateY(' + pscroll + 'px)';
			this.clubs.style.transform = 'translateY(' + cscroll + 'px)';

			if(this.activity_next && !this.fetch_activities)
				if(this.element.offsetHeight + this.element.scrollTop + 800 >= this.middle.offsetHeight)
					this.fetchActivities(this.id, 0, this.activity_last);
		});
	}

	createEditableString(textClass, maxLength = 32, textarea = false){
		const element = createElement('div', {className: 'profile-editable-string-container'});
		const text = createElement('span', {className: textClass});
		const input = createElement(textarea ? 'textarea' : 'input', {className: textClass, attributes: {spellcheck: false, maxlength: maxLength}});
		const button = createElement('svg', {className: 'profile-editable-string-svg'}, [
			createElement('path', {attributes: {fill: 'none', d: 'M0 0h24v24H0z'}}),
			createElement('path', {attributes: {d: 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z'}})
		]);

		const data = {element, text, input, setText(text){
			this.text.setText(text);
			this.input.value = text;
		}};

		element.appendChild(text);
		element.appendChild(input);
		element.appendChild(button);
		button.on('click', () => {
			if(!this.self)
				return;
			element.classList.add('editing');
			input.focus();

			this.updateSize();
		});

		input.on('blur', () => {
			element.classList.remove('editing');

			this.updateSize();

			if(text.textContent != input.value){
				this.edited();

				text.setText(input.value);
			}
		});

		input.on('input', () => {
			input.value = input.value.replace(/[\r\n]+/g, '');
		});

		return data;
	}

	edited(){
		this.submit.style.display = '';
		this.updateSize();
	}

	updateResult(data){
		this.submit.classList.remove('disabled');
		this.submittingToast.hideAfter(2000);
		this.submittingToast.progress(0);

		if(!data || data.error || !data.profile){
			this.submittingToast.text.setText('Could not update profile');

			if(!this.self)
				return;
			const error = (data && data.error) || 'There was an error updating your profile';

			this.error.setText(error);
			this.updateSize();

			return;
		}

		if(data.profile.image){
			if(!this.self)
				return;
			this.error.setText('Could not update image: ' + data.profile.image);
		}else if(this.submittedpfp){
			const url = cdnPath('profile', accountManager.id) + '?nocache=' + Date.now();

			pageManager.topBar.showProfilePhoto(url);

			if(!this.self)
				return;
			setBackgroundImage(this.profileImage, url);
		}

		this.submittingToast.text.setText('Profile updated');
		this.name.setText(data.profile.name);
		this.bio.setText(data.profile.description);
		this.country.setText(data.profile.location_country);
		this.state.setText(data.profile.location_state);
		this.city.setText(data.profile.location_city);
		this.submit.style.display = 'none';

		this.updateSize();
	}

	updateSize(){
		setTimeout(() => {
			this.profileAboutContainer.style.height = 100 + this.details.offsetHeight + 'px';
		}, 0);
	}

	load(data){
		if(!data)
			return pageManager.showErrorPage();
		document.title = data.name + "'s profile";
		history.replaceState(null, document.title, '/profile/' + data.id);

		setBackgroundImage(this.profileImage, cdnPath('profile', data.id));

		this.followRequest = null;
		this.name.setText(data.name);
		this.followcount.setText(data.following_count);
		this.followercount.setText(data.followers_count);
		this.bio.setText(data.description);
		this.country.setText(data.location_country);
		this.state.setText(data.location_state);
		this.city.setText(data.location_city);
		this.fileChooser.value = '';
		this.self = data.self;

		if(this.self){
			if(accountManager.updating)
				this.submit.style.display = '';
			else
				this.submit.style.display = 'none';
			this.profilePhotoContainer.classList.add('editable');
			this.name.element.classList.add('editable');
			this.bio.element.classList.add('editable');
			this.country.element.classList.add('editable');
			this.state.element.classList.add('editable');
			this.city.element.classList.add('editable');
			this.noneText.setText('You are not a part of any clubs');
			this.follow.style.display = 'none';
			this.newClubButton.style.display = '';
		}else{
			this.error.setText('');
			this.profilePhotoContainer.classList.remove('editable');
			this.name.element.classList.remove('editable');
			this.bio.element.classList.remove('editable');
			this.country.element.classList.remove('editable');
			this.state.element.classList.remove('editable');
			this.city.element.classList.remove('editable');
			this.noneText.setText('This user is not a part of any clubs');
			this.follow.style.display = '';
			this.newClubButton.style.display = 'none';
			this.isFollowing = data.following;

			if(this.isFollowing)
				this.follow.setText('Unfollow');
			else
				this.follow.setText('Follow');
		}

		this.updateSize();

		while(this.clubsList.childNodes.length)
			this.clubsList.removeChild(this.clubsList.childNodes[0]);
		var club_count = 0;
		for(var i in data.clubs){
			club_count++;

			const club = data.clubs[i];
			const container = createElement('a', {className: 'profile-clubs-photo-container', attributes: {href: '/club/' + club.id}});
			const img = createElement('div', {className: 'profile-clubs-photo'});

			setBackgroundImage(img, cdnPath('club', club.id));
			ajaxify(container);

			container.appendChild(img);

			this.clubsList.appendChild(container);
		}

		this.noneText.style.display = club_count ? 'none' : '';

		if(this.fetch_activities)
			this.fetch_activities.abort();
		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(this.hasCenterLoading){
			this.centerLoadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.centerLoadingIndicator);
			this.hasCenterLoading = false;
		}

		while(this.middle.childNodes.length)
			this.middle.removeChild(this.middle.childNodes[0]);
		this.activity_last = null;
		this.activity_next = false;
		this.id = data.id;
		this.fetchActivities(data.id);

		if(!this.hasCenterLoading){
			this.centerLoadingIndicator.appendChild(this.svg);
			this.middle.appendChild(this.centerLoadingIndicator);
			this.hasCenterLoading = true;
		}
	}

	showActivities(list){
		if(this.hasCenterLoading){
			this.middle.removeChild(this.centerLoadingIndicator);
			this.middle.appendChild(createElement('div', {className: 'profile-container top-padding'}));
			this.hasCenterLoading = false;
		}

		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(!list){
			this.middle.appendChild(createElement('span', {className: 'profile-activity-error text', innerText: 'Could not load activities at this moment'}));

			return;
		}else{
			for(var i = 0; i < list.length; i++){
				this.activity_last = list[i].id;
				this.middle.appendChild(activityManager.createActivity(list[i]));
			}

			if(list.length){
				this.activity_next = true;

				if(!this.hasLoading){
					this.loadingIndicator.appendChild(this.svg);
					this.middle.appendChild(this.loadingIndicator);
					this.hasLoading = true;
				}
			}else if(!this.activity_last){
				this.middle.appendChild(createElement('span', {className: 'profile-activity-error text', innerText: this.self ?
					'You don\'t have any activities': 'This user has no activities'
				}));
			}
		}
	}

	fetchActivities(id, top = 50, last){
		this.fetch_activities = accountManager.sendRequest('/activities?id=' + id + '&top=' + top + (last ? '&last=' + last : ''), null, (status, error, resp) => {
			this.activity_next = false;
			this.fetch_activities = null;

			if(error || status != 200)
				this.showActivities(null);
			else
				this.showActivities(resp.activities);
		});
	}

	hidden(){
		if(this.fetch_activities){
			this.fetch_activities.abort();
			this.fetch_activities = null;
		}
	}
}

const activityManager = new (class{
	constructor(){
		this.activityParticipate = {};
		this.attending = {};
		this.errorToast = new Toast("Could not change participation in activity");

		toastManager.addToast(this.errorToast);
	}

	changeParticipate(id, resultcb){
		const existing = this.activityParticipate[id];
		const change = !this.attending[id];

		if(existing && existing.change != change){
			existing.callback(change);
			existing.request.abort();

			this.attending[id] = change;

			return resultcb(change);
		}

		this.activityParticipate[id] = {change, request: accountManager.sendRequest('/add_to_activity', {activity: id, addToActivity: change ? true : false}, (status, error, resp) => {
			this.activityParticipate[id] = null;

			if(status != 200 || error){
				this.errorToast.show();
				this.errorToast.hideAfter(2000);
				this.attending[id] = !change;

				resultcb(!change);

				return;
			}

			resultcb(change);
		}), callback: resultcb};

		this.attending[id] = change;
	}

	createOffsetContainer(left = true){
		const container = createElement('div', {className: 'profile-activity-container offset ' + (left ? 'left' : 'right')});

		container.appendChild(createElement('div', {className: 'offset'}));

		return container;
	}

	createActivity(data){
		const body = createElement('div', {className: 'profile-activity shadow-light'});

		do{
			const container = createElement('div', {className: 'profile-activity-container'});
			const left = createElement('div', {className: 'profile-activity-container left'});
			const right = createElement('div', {className: 'profile-activity-container right'});

			const creatorPhoto = createElement('a', {className: 'profile-activity-creator-photo', attributes: {href: '/profile/' + data.creator.id}});

			setBackgroundImage(creatorPhoto, cdnPath('profile', data.creator.id));
			ajaxify(creatorPhoto);

			left.appendChild(creatorPhoto);
			left.appendChild(createElement('span', {className: 'text metro', innerText: data.creator.name}));

			if(data.club){
				const clubPhoto = createElement('a', {className: 'profile-activity-club-photo', attributes: {href: '/club/' + data.club.id}});

				setBackgroundImage(clubPhoto, cdnPath('club', data.club.id));
				ajaxify(clubPhoto);

				right.appendChild(clubPhoto);
				right.appendChild(createElement('span', {className: 'text metro', innerText: data.club.name}));
			}

			container.appendChild(left);
			container.appendChild(right);
			body.appendChild(container);
		}while(false);

		var ctime = Date.now();
		do{
			const title = this.createOffsetContainer();
			const link = createElement('a', {className: 'profile-activity-title text metro', innerText: data.name, attributes: {href: '/activity/' + data.id}});

			title.appendChild(link);
			body.appendChild(title);

			ajaxify(link);

			const time = this.createOffsetContainer();
			var now = ctime;
			var text;

			if(now < data.time_start){
				const date = new Date(data.time_start);
				var hours = date.getHours();
				var am = hours < 12;

				if(hours > 12)
					hours -= 12;
				else if(hours == 0)
					hours = 12;
				text = numberToMonth[date.getMonth()] + ' ' + date.getDate() + ' at ' + hours + ':';

				if(date.getMinutes() < 10)
					text += '0' + date.getMinutes();
				else
					text += date.getMinutes();
				text += ' ' + (am ? 'AM' : 'PM');
			}else if(now < data.time_end)
				text = 'Ongoing now';
			else{
				const date = new Date(data.time_end);

				now = new Date(now);

				var years = now.getYear() - date.getYear();

				if(now.getMonth() < date.getMonth())
					years--;
				else if(now.getMonth() == date.getMonth() && now.getDate() < date.getDate())
					years--;
				if(years){
					if(years > 1)
						text = years + ' years ago';
					else
						text = 'A year ago';
				}else{
					var months = now.getMonth() - date.getMonth();

					if(months < 0)
						months += 12;
					if(now.getDate() < date.getDate())
						months--;
					if(months){
						if(months > 1)
							text = months + ' months ago';
						else
							text = 'A month ago';
					}else{
						var hours = (now.getTime() - date.getTime()) / (3600 * 1000);
						var days = Math.floor(hours / 24);

						hours = Math.floor(hours % 24);

						if(days){
							if(days > 1)
								text = days + ' days ago';
							else
								text = 'Yesterday';
						}else if(hours){
							if(hours > 1)
								text = hours + ' hours ago';
							else
								text = 'An hour ago';
						}else
							text = 'Just finished';
					}
				}
			}

			time.appendChild(createElement('div', {className: 'profile-activity-time text metro', innerText: data.type + ' \u2022 ' + text}));
			body.appendChild(time);

			const description = this.createOffsetContainer();

			description.appendChild(createElement('span', {className: 'profile-activity-description text', innerText: data.description}));
			body.appendChild(description);
		}while(false);

		do{
			const container = this.createOffsetContainer();
			const map = createElement('div', {className: 'profile-activity-location shadow-light'});

			container.appendChild(map);
			body.appendChild(container);

			window.waitForMap(() => {
				var gmap = new google.maps.Map(map, {
					center: {lat: data.latitude, lng: data.longitude},
					zoom: 18
				});

				new google.maps.Marker({
					position: {lat: data.latitude, lng: data.longitude},
					map: gmap,
					title: data.name
				});
			});
		}while(false);

		do{
			const textContainer = this.createOffsetContainer();
			const text = this.createOffsetContainer();

			text.appendChild(createElement('span', {className: 'text metro', innerText: data.time_end > ctime ? (data.attending.length ? 'People Attending' : 'No one is attending yet. Be the first!') :
																												(data.attending.length ? 'People Attended' : '')}));
			body.appendChild(textContainer);
			textContainer.appendChild(text);

			const attending = this.createOffsetContainer();
			var userIsAttending = false;

			for(var i = 0; i < data.attending.length; i++){
				if(data.attending[i].id == accountManager.id)
					userIsAttending = true;
				const el = createElement('a', {className: 'profile-activity-attending-photo', attributes: {href: '/profile/' + data.attending[i].id}});

				ajaxify(el);
				setBackgroundImage(el, cdnPath('profile', data.attending[i].id));

				attending.appendChild(el);
			}

			this.attending[data.id] = userIsAttending;

			if(data.time_start > ctime){
				const buttonContainer = this.createOffsetContainer(false);
				const button = createElement('div', {className: 'profile-activity-attend-button text metro', innerText: userIsAttending ? 'unattend' : 'join'});

				buttonContainer.appendChild(button);
				textContainer.appendChild(buttonContainer);

				button.on('click', () => {
					if(!this.attending[data.id])
						button.setText('unattend');
					else
						button.setText('join');
					button.classList.add('animation');

					this.changeParticipate(data.id, (attending) => {
						button.classList.remove('animation');

						if(attending)
							button.setText('unattend');
						else
							button.setText('join');
					});
				});
			}

			body.appendChild(attending);
		}while(false);

		return body;
	}
});

const types = [
	"Fundraising", "Garbage Cleanup", "Charity Work", "Environmental Care",
	"Environmental Cleanup", "Hospital Volunteering", "Animal Care", "Awareness Work",
	"Religious Work", "Religious Volunteering", "Mentorship Volunteering", "Elderly Care",
	"Disabled Care", "Special Needs Care", "Homeless Volunteer Work", "Social Issue Awareness",
	"Protest", "Educational Workshops"
];

class ActivityCreationPage extends Page{
	constructor(){
		super();

		this.creatingToast = new LoadingToast('Creating activity');
		this.creatingToast.indeterminate();

		toastManager.addToast(this.creatingToast);

		this.form = createElement('div', {className: 'new-activity-form shadow-heavy'});
		this.formContainer = createElement('div', {className: 'new-activity-form-container'});
		this.leftFormContainer = createElement('div', {className: 'new-activity-form-container column'});
		this.rightFormContainer = createElement('div', {className: 'new-activity-form-container column'});
		this.title = createElement('span', {className: 'new-activity-form-title text metro', innerText: 'Create an Activity'});
		this.leftFormContainer.appendChild(this.title);
		this.entries = createElement('div', {className: 'new-activity-form-entries'});
		this.title = this.createEntry('Title', 'text');
		this.entries.appendChild(this.title.entry);
		this.type = this.createTypeEntry();
		this.entries.appendChild(this.type.entry);
		this.description = this.createEntry('Description', 'text', true);
		this.entries.appendChild(this.description.entry);
		this.time = this.createTimeEntry('Start time');
		this.entries.appendChild(this.time.entry);
		this.endtime = this.createTimeEntry('End time');
		this.entries.appendChild(this.endtime.entry);
		this.club = this.createClubDropdown();
		this.entries.appendChild(this.club.entry);
		this.leftFormContainer.appendChild(this.entries);
		this.button = createElement('div', {className: 'new-activity-button text metro', innerText: 'Create'});
		this.leftFormContainer.appendChild(this.button);
		this.actionError = createElement('span', {className: 'new-activity-form-entry-error text metro'});
		this.leftFormContainer.appendChild(this.actionError);
		this.successText = createElement('span', {className: 'text metro'});

		this.formContainer.appendChild(this.leftFormContainer);
		this.formContainer.appendChild(this.rightFormContainer);
		this.form.appendChild(this.formContainer);
		this.element.appendChild(createElement('div', {className: 'center'}, [this.form]));
		this.element.classList.add('login-background');

		this.showingsuccess = false;
		this.gmapmarker = null;
		this.gmap = null;
		this.selectedLocation = null;
		this.map = createElement('div', {className: 'new-activity-location shadow-light'});
		this.rightFormContainer.appendChild(this.map);
		this.mapError = createElement('span', {className: 'new-activity-form-entry-error text metro'});
		this.rightFormContainer.appendChild(this.mapError);
		this.submitting = null;

		window.waitForMap(() => {
			this.gmap = new google.maps.Map(this.map, {
				zoom: 11,
				center: {lat: 37.330794, lng: -121.9324748},
			});

			google.maps.event.addListener(this.gmap, 'click', (e) => {
				if(this.gmapmarker)
					this.gmapmarker.setMap(null);
				this.selectedLocation = {lat: e.latLng.lat(), lng: e.latLng.lng()};
				this.gmapmarker = new google.maps.Marker({
					position: this.selectedLocation,
					map: this.gmap,
					title: 'Activity Location'
				});
			});
		});

		this.button.on('click', () => {
			this.trySubmit();
		});

		this.clubs = null;
		this.addClubPrompt = createElement('a', {className: 'new-activity-form-entry-dropdown-value text metro', innerText: 'Want to associate with a club? Create one', attributes: {href: '/new_club'}});
		this.noClub = createElement('span', {className: 'new-activity-form-entry-dropdown-value text metro'});

		ajaxify(this.addClubPrompt);
	}

	load(data){
		this.clubs = null;

		if(data.clubs){
			this.clubs = [];

			const list = this.club.list;

			while(list.childNodes.length)
				list.removeChild(list.childNodes[0]);
			for(var i in data.clubs){
				let id = data.clubs[i].id;
				let name = data.clubs[i].name;
				const el = createElement('div', {className: 'new-activity-form-entry-club-container new-activity-form-entry-dropdown-value'});
				const photo = createElement('div', {className: 'new-activity-form-entry-club-photo'});
				const button = createElement('svg', {className: 'new-activity-form-entry-club-close'}, [
					createElement('path', {attributes: {d: 'M0 0h24v24H0z', fill: 'none'}}),
					createElement('path', {attributes: {d: 'M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z'}})
				]);

				setBackgroundImage(photo, cdnPath('club', id));

				el.appendChild(photo);
				el.appendChild(createElement('div', {className: 'text metro', innerText: name}));
				el.appendChild(button);
				list.appendChild(el);

				el.on('mousedown', (e) => {
					e.preventDefault();

					return false;
				});

				el.on('click', () => {
					if(this.club.selected)
						return;
					el.classList.add('chosen');

					this.club.selected = id;
					this.club.entry.removeChild(this.club.cont);
					this.club.entry.appendChild(el);
					this.club.selected = {el, name, id};
				});

				button.on('click', (e) => {
					e.stopPropagation();
					el.classList.remove('chosen');

					this.club.selected = null;
					this.club.entry.appendChild(this.club.cont);
					this.club.entry.removeChild(el);

					list.appendChild(el);

					return false;
				});

				this.clubs.push({name, el, id});
			}

			if(!this.clubs.length)
				list.appendChild(this.addClubPrompt);
			if(this.club.selected){
				this.club.entry.removeChild(this.club.selected.el);
				this.club.entry.appendChild(this.club.cont);
				this.club.selected = null;
			}
		}
	}

	createClubDropdown(){
		const entry = createElement('div', {className: 'new-activity-form-entry', css: {height: '80px'}});
		const cont = createElement('div', {className: 'new-activity-form-entry-input-container'});
		const field = createElement('input', {className: 'new-activity-form-entry-input text metro', attributes: {placeholder: 'Enter the club (optional)', spellcheck: false}});

		entry.appendChild(createElement('span', {className: 'text metro', innerText: 'Club'}));
		cont.appendChild(field);
		cont.appendChild(createElement('div', {className: 'new-activity-form-entry-input-focus-visualizer login-background'}));
		entry.appendChild(cont);

		const list = createElement('div', {className: 'new-activity-form-entry-dropdown-values clublist shadow-heavy', css: {top: 'calc(100% + 4px)', maxHeight: '120px'}});

		field.on('input', () => {
			if(!this.clubs)
				return;
			list.style.display = 'block';

			if(!this.clubs.length)
				return;
			while(list.childNodes.length)
				list.removeChild(list.childNodes[0]);

			var count = 0;

			for(var i = 0; i < this.clubs.length; i++)
				if(this.clubs[i].name.startsWith(field.value)){
					count++;
					list.appendChild(this.clubs[i].el);
				}
			if(!count){
				list.appendChild(this.noClub);
				this.noClub.setText('You are not in any clubs starting with ' + field.value);
			}
		});

		field.on('focus', () => {
			list.style.display = 'block';
		});

		field.on('blur', () => {
			list.style.display = '';
		});

		cont.appendChild(list);

		return {entry, selected: null, cont, list, field};
	}

	createTypeEntry(){
		const entry = createElement('div', {className: 'new-activity-form-entry'});
		const cont = createElement('div', {className: 'new-activity-form-entry-input-container row'});
		const error = createElement('span', {className: 'new-activity-form-entry-error text metro'});
		const input = this.createDropdown(types, null, 'Select an activity type', 'long');
		const field = {get value(){
			if(input.selectedIndex == -1)
				return '';
			return types[input.selectedIndex];
		}, set value(v){
			input.updateValues([]);
			input.updateValues(types);
		}};

		entry.appendChild(createElement('span', {className: 'text metro', innerText: 'Type'}));
		cont.appendChild(input.entry);
		entry.appendChild(cont);
		entry.appendChild(error);
		input.entry.style.border = 'none';
		input.entry.style.borderBottom = '1px #ccc solid';
		input.entry.style.height = '41px';

		return {entry, field, error};
	}

	createEntry(name, type, large = false){
		const entry = createElement('div', {className: 'new-activity-form-entry' + (large ? ' large' : '')});
		const cont = createElement('div', {className: 'new-activity-form-entry-input-container'});
		const field = createElement(large ? 'textarea' : 'input', {className: 'new-activity-form-entry-input text metro', attributes: {placeholder: 'Enter the activity ' + name.toLowerCase(), type, spellcheck: false}});
		const error = createElement('span', {className: 'new-activity-form-entry-error text metro'});

		entry.appendChild(createElement('span', {className: 'text metro', innerText: name}));
		cont.appendChild(field);
		cont.appendChild(createElement('div', {className: 'new-activity-form-entry-input-focus-visualizer login-background'}));
		entry.appendChild(cont);
		entry.appendChild(error);
		field.on('keyup', (e) => {
			if(e.keyCode == 13)
				this.trySubmit();
		});

		return {entry, field, error};
	}

	createDropdown(valuesList, defaultv, placeholder, attr){
		var selectedIndex = -1;
		if(defaultv !== undefined && defaultv !== null)
			selectedIndex = defaultv;
		const entry = createElement('div', {className: 'new-activity-form-entry-dropdown' + (attr ? ' ' + attr : ''), attributes: {tabindex: 0}});
		const text = createElement('div', {className: 'new-activity-form-entry-dropdown-text text metro placeholder', innerText: selectedIndex != -1 ? valuesList[defaultv] : placeholder});
		const svg = createElement('svg', {className: 'new-activity-form-entry-dropdown-svg'}, [
			createElement('path', {attributes: {fill: 'none', d: 'M0 0h24v24H0z'}}),
			createElement('path', {attributes: {d: 'M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z'}})
		]);

		const values = createElement('div', {className: 'new-activity-form-entry-dropdown-values shadow-heavy'});

		var hasFocus = false;

		entry.appendChild(svg);
		entry.appendChild(text);
		entry.appendChild(values);

		entry.on('click', () => {
			hasFocus = !hasFocus;

			if(hasFocus){
				entry.classList.add('choosing');
				entry.focus();
			}else{
				entry.classList.remove('choosing');
				entry.blur();
			}
		});

		entry.on('blur', () => {
			entry.classList.remove('choosing');
			hasFocus = false;
		});

		const data = {entry, selectedIndex, updateValues: (valuesList) => {
			while(values.childNodes.length)
				values.removeChild(values.childNodes[0]);
			for(let i = 0; i < valuesList.length; i++){
				const el = createElement('div', {className: 'new-activity-form-entry-dropdown-value text metro', innerText: valuesList[i]});

				values.appendChild(el);
				el.on('click', () => {
					data.selectedIndex = i;
					text.classList.remove('placeholder');
					text.setText(valuesList[i]);

					if(data.chosen)
						data.chosen();
				});
			}

			if(data.selectedIndex >= valuesList.length){
				data.selectedIndex = -1;
				text.classList.add('placeholder');
				text.setText(placeholder);
			}
		}};

		data.updateValues(valuesList);

		return data;
	}

	createTimeEntry(name){
		const days = [];

		for(var i = 0; i < 31; i++)
			days.push(i + 1);
		const now = new Date();
		var monthdays = new Date();

		monthdays.setMonth(monthdays.getMonth() + 1);
		monthdays.setDate(0);
		monthdays = monthdays.getDate();

		const entry = createElement('div', {className: 'new-activity-form-entry large'});
		const cont = createElement('div', {className: 'new-activity-form-entry-input-container row'});
		const error = createElement('span', {className: 'new-activity-form-entry-error text metro'});
		const month = this.createDropdown(numberToMonth, now.getMonth());
		const day = this.createDropdown(days.slice(0, monthdays), now.getDate() - 1, 'Select a day', 'short');
		const year = this.createDropdown([now.getFullYear(), now.getFullYear() + 1], 0, '', 'short');

		entry.appendChild(createElement('span', {className: 'text metro', innerText: name}));
		cont.appendChild(month.entry);
		cont.appendChild(day.entry);
		cont.appendChild(year.entry);
		entry.appendChild(cont);
		entry.appendChild(error);

		month.chosen = () => {
			monthdays = new Date(now.getYear() + year.selectedIndex, month.selectedIndex + 1, 0).getDate();

			day.updateValues(days.slice(0, monthdays));
		};

		year.chosen = month.chosen;

		const hours = [];
		const minutes = [];

		for(var i = 0; i < 12; i++)
			hours.push(i + 1);
		for(var i = 0; i < 12; i++)
			minutes.push((i * 5) + '');
		var hourz = now.getHours() % 12;

		if(hourz == 0)
			hourz = 12;
		const hour = this.createDropdown(hours, hourz - 1, '', 'short');
		const minute = this.createDropdown(minutes, Math.floor(now.getMinutes() / 5), '', 'short');
		const ampm = this.createDropdown(['AM', 'PM'], now.getHours() >= 12 ? 1 : 0, '', 'short');

		cont.appendChild(hour.entry);
		cont.appendChild(minute.entry);
		cont.appendChild(ampm.entry);

		return {entry, fields: {month, day, year}, error, calculateTime(){
			if(day.selectedIndex == -1)
				return null;
			var hours = hour.selectedIndex + 1;

			if(ampm.selectedIndex == 0){
				if(hours == 12)
					hours = 0;
			}else{
				if(hours != 12)
					hours += 12;
			}

			return new Date(now.getFullYear() + year.selectedIndex, month.selectedIndex, day.selectedIndex + 1, hours, minute.selectedIndex * 5);
		}};
	}

	showing(){
		this.title.field.value = '';
		this.type.field.value = '';
		this.description.field.value = '';
		this.club.field.value = '';
		this.type.field.value = '';
		this.selectedLocation = null;

		this.title.error.setText('');
		this.type.error.setText('');
		this.description.error.setText('');
		this.mapError.setText('');
		this.time.error.setText('');
		this.endtime.error.setText('');

		this.form.style.width = '';

		if(this.gmapmarker){
			this.gmapmarker.setMap(null);
			this.gmapmarker = null;
		}

		if(this.showingsuccess){
			this.showingsuccess = false;
			this.form.appendChild(this.formContainer);
			this.form.removeChild(this.successText);
		}
	}

	trySubmit(){
		if(this.submitting)
			return;
		this.actionError.setText('');

		var error = false;

		if(this.title.field.value)
			this.title.error.setText('');
		else{
			this.title.error.setText('Enter the title');

			error = true;
		}

		if(this.type.field.value)
			this.type.error.setText('');
		else{
			this.type.error.setText('Enter the type');

			error = true;
		}

		if(this.description.field.value)
			this.description.error.setText('');
		else{
			this.description.error.setText('Enter a description');

			error = true;
		}

		if(this.selectedLocation)
			this.mapError.setText('');
		else{
			this.mapError.setText('Select a location');

			error = true;
		}

		var time_start = this.time.calculateTime();
		var time_end = this.endtime.calculateTime();

		if(time_start == null){
			this.time.error.setText('Select a valid time');

			error = true;
		}else
			time_start = time_start.getTime();
		if(time_end == null){
			this.endtime.error.setText('Select a valid time');

			error = true;
		}else
			time_end = time_end.getTime();
		if(time_start && time_end && time_start >= time_end){
			this.time.error.setText('Must be less than end time');
			this.endtime.error.setText('Must be greater than start time');

			error = true;
		}else{
			this.time.error.setText('');
			this.endtime.error.setText('');
		}

		if(error)
			return;
		this.button.classList.add('disabled');
		this.creatingToast.text.setText('Creating activity');
		this.creatingToast.show();
		this.creatingToast.indeterminate();

		this.submitting = accountManager.sendRequest('/activity_create', {title: this.title.field.value, type: this.type.field.value, description: this.description.field.value,
			latitude: this.selectedLocation.lat, longitude: this.selectedLocation.lng, time_start, time_end, club: this.club.selected ? this.club.selected.id : null}, (status, error, data) => {
				this.submitting = null;
				this.button.classList.remove('disabled');
				this.creatingToast.hideAfter(2000);
				this.creatingToast.progress();

				if(error || status != 200 || (data && (data.error || !data.activity))){
					this.actionError.setText((data && data.error) || 'There was an error creating this activity, try again later');
					this.creatingToast.text.setText('Could not create the activity');
				}else{
					if(!this.showingsuccess){
						this.showingsuccess = true;
						this.form.removeChild(this.formContainer);
						this.form.appendChild(this.successText);
						this.form.style.width = '320px';
						this.successText.setText('Activity created, you will be shortly redirected to it');
						this.creatingToast.text.setText('Activity created');

						pageLoader.load('/activity/' + data.activity.id);
					}
				}
			});
	}
}

class FeedPage extends Page{
	constructor(){
		super();

		this.fetch_activities = null;
		this.activity_last = null;
		this.activity_next = false;

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

		this.svg = createElement('svg', {className: 'activities-loader'});

		generateGradient(this.svg, {gradient: 'activities-gradient', mask: 'activities-mask'}, [
			{
				offset: "0%",
				color: "#0f0"
			},
			{
				offset: "100%",
				color: "#00f"
			}
		], [
			createElement('circle', {attributes: {cx: 50, cy: 50, r: 16}})
		]);

		this.hasCenterLoading = false;
		this.centerLoadingIndicator = createElement('div', {className: 'center'});
		this.hasLoading = false;
		this.loadingIndicator = createElement('div', {className: 'activities-loader-center'});

		this.element.on('scroll', (e) => {
			if(this.activity_next && !this.fetch_activities)
				if(this.element.offsetHeight + this.element.scrollTop + 800 >= this.middle.offsetHeight)
					this.fetchActivities(0, this.activity_last);
		});
	}

	load(data){
		document.title = 'Your feed';
		history.replaceState(null, document.title, '/');

		if(this.fetch_activities)
			this.fetch_activities.abort();
		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(this.hasCenterLoading){
			this.centerLoadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.centerLoadingIndicator);
			this.hasCenterLoading = false;
		}

		while(this.middle.childNodes.length)
			this.middle.removeChild(this.middle.childNodes[0]);
		this.activity_last = null;
		this.activity_next = false;
		this.fetch_activities = null;

		if(!this.hasCenterLoading){
			this.centerLoadingIndicator.appendChild(this.svg);
			this.middle.appendChild(this.centerLoadingIndicator);
			this.hasCenterLoading = true;
		}

		this.showActivities(data && data.activities);
	}

	showActivities(list){
		if(this.hasCenterLoading){
			this.middle.removeChild(this.centerLoadingIndicator);
			this.middle.appendChild(createElement('div', {className: 'profile-container top-padding'}));
			this.hasCenterLoading = false;
		}

		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(!list){
			this.middle.appendChild(createElement('span', {className: 'profile-activity-error text', innerText: 'Could not load activities at this moment'}));

			return;
		}else{
			for(var i = 0; i < list.length; i++){
				this.activity_last = list[i].id;
				this.middle.appendChild(activityManager.createActivity(list[i]));
			}

			if(list.length){
				this.activity_next = true;

				if(!this.hasLoading){
					this.loadingIndicator.appendChild(this.svg);
					this.middle.appendChild(this.loadingIndicator);
					this.hasLoading = true;
				}
			}else if(!this.activity_last)
				this.middle.appendChild(createElement('span', {className: 'profile-activity-error text', innerText: 'There are no activities in your feed. Begin by following someone' }));
		}
	}

	fetchActivities(top = 50, last){
		this.fetch_activities = accountManager.sendRequest('/activity_feed?top=' + top + (last ? '&last=' + last : ''), null, (status, error, resp) => {
			this.activity_next = false;
			this.fetch_activities = null;

			if(error || status != 200)
				this.showActivities(null);
			else
				this.showActivities(resp.activities);
		});
	}

	hidden(){
		if(this.fetch_activities){
			this.fetch_activities.abort();
			this.fetch_activities = null;
		}
	}
}

class ActivityPage extends Page{
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
		if(!data)
			return pageManager.showErrorPage();
		while(this.middle.childNodes.length)
			this.middle.removeChild(this.middle.childNodes[0]);
		this.middle.appendChild(createElement('div', {className: 'profile-container top-padding'}));
		this.middle.appendChild(activityManager.createActivity(data.activity));

		document.title = data.activity.name;
		history.replaceState(null, document.title, '/activity/' + data.activity.id);
	}
}

class ClubCreationPage extends Page{
	constructor(){
		super();

		this.showingsuccess = false;
		this.creatingToast = new LoadingToast("Creating club");

		toastManager.addToast(this.creatingToast);

		this.form = createElement('div', {className: 'login-form shadow-heavy', css: {width: '640px'}});
		this.title = createElement('span', {className: 'login-form-title text metro', innerText: 'Create a Club'});
		this.form.appendChild(this.title);
		this.formContainer = createElement('div', {className: 'login-form-container'});
		this.entries = createElement('div', {className: 'login-form-entries'});
		this.name = this.createEntry('Name', 'text');
		this.entries.appendChild(this.name.entry);
		this.type = this.createEntry('Type', 'text');
		this.entries.appendChild(this.type.entry);
		this.description = this.createEntry('Description', 'text', this.passwordIcon);
		this.entries.appendChild(this.description.entry);
		this.entries.appendChild(createElement('div', {className: 'login-form-entry break'}));
		this.country = this.createEntry('Country', 'text');
		this.entries.appendChild(this.country.entry);
		this.state = this.createEntry('Province/State', 'text');
		this.entries.appendChild(this.state.entry);
		this.city = this.createEntry('City', 'text');
		this.entries.appendChild(this.city.entry);
		this.fileChooser = createElement('input', {attributes: {type: 'file', name: 'name', accept: 'image/*'}, css: {display: 'none'}});
		this.setPhoto = createElement('span', {className: 'new-club-set-photo-button text metro', innerText: 'Set club photo'});
		this.setPhotoError = createElement('span', {className: 'login-form-entry-error text metro'});
		this.setPhotoContainer = createElement('div', {className: 'new-club-set-photo-container'});
		this.setPhotoContainer.appendChild(this.setPhoto);
		this.setPhotoContainer.appendChild(this.setPhotoError);
		this.setPhotoContainer.appendChild(this.fileChooser);
		this.photo = createElement('div', {className: 'new-club-photo', css: {display: 'none'}});
		this.photoContainer = createElement('div', {className: 'new-club-photo-container'});
		this.photoContainer.appendChild(this.photo);
		this.photoContainer.appendChild(this.setPhotoContainer);
		this.formContainer.appendChild(this.entries);
		this.formContainer.appendChild(this.photoContainer);
		this.button = createElement('div', {className: 'login-button text metro', innerText: 'create'});
		this.formContainer.appendChild(this.button);
		this.actionError = createElement('span', {className: 'login-form-entry-error text metro'});
		this.formContainer.appendChild(this.actionError);
		this.successText = createElement('span', {className: 'text metro', css: {marginTop: '40px'}});
		this.form.appendChild(this.formContainer);
		this.element.appendChild(createElement('div', {className: 'center'}, [this.form]));
		this.element.classList.add('login-background');

		this.actionError.style.display = 'none';

		this.button.on('click', () => {
			this.trySubmit();
		});

		this.setPhoto.on('click', () => {
			this.fileChooser.click();
		});

		this.submitPicture = null;
		this.fileChooser.on('change', (file) => {
			var input = file.target;
			var reader = new FileReader();

			reader.onload = () => {
				this.submitPicture = reader.result;
				this.photo.style.display = '';

				setBackgroundImage(this.photo, reader.result);
			};

			reader.readAsDataURL(input.files[0]);
		});
	}

	createEntry(name, type, icon){
		const entry = createElement('div', {className: 'login-form-entry'});
		const cont = createElement('div', {className: 'login-form-entry-input-container'});
		const field = createElement('input', {className: 'login-form-entry-input text metro', attributes: {placeholder: 'Enter the club ' + name.toLowerCase(), type, spellcheck: false}});
		const error = createElement('span', {className: 'login-form-entry-error text metro'});

		entry.appendChild(createElement('span', {className: 'text metro', innerText: name}));

		if(icon)
			cont.appendChild(icon);
		cont.appendChild(field);
		cont.appendChild(createElement('div', {className: 'login-form-entry-input-focus-visualizer login-background'}));
		entry.appendChild(cont);
		entry.appendChild(error);
		field.on('keyup', (e) => {
			if(e.keyCode == 13)
				this.trySubmit();
		});

		return {entry, field, error};
	}

	showing(){
		this.type.field.value = '';
		this.description.field.value = '';
		this.name.field.value = '';
		this.country.field.value = '';
		this.state.field.value = '';
		this.city.field.value = '';
		this.photo.style.display = 'none';

		this.name.error.setText('');
		this.type.error.setText('');
		this.description.error.setText('');
		this.country.error.setText('');
		this.city.error.setText('');
		this.city.error.setText('');
		this.setPhotoError.setText('');
		this.submitPicture = null;
		this.fileChooser.value = '';

		if(this.showingsuccess){
			this.showingsuccess = false;
			this.form.appendChild(this.formContainer);
			this.form.removeChild(this.successText);
		}
	}

	load(){
		document.title = 'Create a Club';
		history.replaceState(null, document.title, '/new_club');
	}

	trySubmit(){
		if(this.submitting)
			return;
		this.actionError.style.display = 'none';

		var error = false;

		if(this.type.field.value)
			this.type.error.setText('');
		else{
			this.type.error.setText('Enter a type');

			error = true;
		}

		if(this.description.field.value)
			this.description.error.setText('');
		else{
			this.description.error.setText('Enter a description');

			error = true;
		}

		if(this.name.field.value)
			this.name.error.setText('');
		else{
			this.name.error.setText('Enter a name');
			error = true;
		}

		if(this.country.field.value)
			this.country.error.setText('');
		else{
			this.country.error.setText('Enter a valid country');
			error = true;
		}

		if(this.state.field.value)
			this.state.error.setText('');
		else{
			this.state.error.setText('Enter a valid state');
			error = true;
		}

		if(this.city.field.value)
			this.city.error.setText('');
		else{
			this.city.error.setText('Enter a valid city');
			error = true;
		}

		if(this.submitPicture)
			this.setPhotoError.setText('');
		else{
			this.setPhotoError.setText('Select a photo');
			error = true;
		}

		if(error)
			return;
		this.button.classList.add('disabled');
		this.creatingToast.show();
		this.creatingToast.text.setText('Creating Club');
		this.creatingToast.indeterminate();
		this.submitting = accountManager.sendRequest('/club_create', {name: this.name.field.value, description: this.description.field.value, type: this.type.field.value,
			country: this.country.field.value, state: this.state.field.value, city: this.city.field.value, image: this.submitPicture}, (status, error, data) => {
				this.submitting = null;
				this.submitting = null;
				this.button.classList.remove('disabled');
				this.creatingToast.hideAfter(2000);
				this.creatingToast.progress();

				if(error || status != 200 || (data && (data.error || !data.club))){
					this.actionError.setText((data && data.error) || 'There was an error creating this club, try again later');
					this.creatingToast.text.setText('Could not create the club');
				}else{
					if(!this.showingsuccess){
						this.showingsuccess = true;
						this.form.removeChild(this.formContainer);
						this.form.appendChild(this.successText);
						this.successText.setText('Club created, you will be shortly redirected to it');
						this.creatingToast.text.setText('Club created');

						pageLoader.load('/club/' + data.club.id);
					}
				}
			});
	}
}

class ClubPage extends Page{
	constructor(){
		super();

		this.joinFailed = new LoadingToast('Failed to join/leave the club, try again');

		toastManager.addToast(this.joinFailed);

		this.id = null;

		this.table = createElement('div', {className: 'profile-table'});
		this.left = createElement('div', {className: 'profile-container left'});
		this.middle = createElement('div', {className: 'profile-container middle'});
		this.right = createElement('div', {className: 'profile-container right'});
		this.left.appendChild(createElement('div', {className: 'profile-container top-padding'}));
		this.right.appendChild(createElement('div', {className: 'profile-container top-padding'}));

		this.table.appendChild(this.left);
		this.table.appendChild(createElement('div', {className: 'profile-container expander'}));
		this.table.appendChild(this.middle);
		this.table.appendChild(createElement('div', {className: 'profile-container expander'}));
		this.table.appendChild(this.right);
		this.element.appendChild(this.table);

		this.clubAboutContainer = createElement('div', {className: 'profile-about-container shadow-light'});
		this.clubImage = createElement('div', {className: 'club-photo shadow-heavy'});
		this.clubPhotoContainer = createElement('div', {className: 'club-photo-container'});
		this.clubPhotoContainer.appendChild(this.clubImage);
		this.clubAboutContainer.appendChild(this.clubPhotoContainer);
		this.details = createElement('div', {className: 'profile-details'});
		this.clubAboutContainer.appendChild(this.details);
		this.name = createElement('div', {className: 'profile-name text'});
		this.details.appendChild(this.name);

		this.details.appendChild(createElement('div', {className: 'divider'}));
		this.descContainer = createElement('div', {className: 'club-description-container'});
		this.type = createElement('span', {className: 'text'});
		this.descContainer.appendChild(this.type);
		this.description = createElement('span', {className: 'profile-bio text', css: {marginTop: '8px'}});
		this.descContainer.appendChild(this.description);
		this.details.appendChild(this.descContainer);
		this.details.appendChild(createElement('div', {className: 'divider'}));
		this.location = createElement('div', {className: 'profile-location'});
		this.details.appendChild(this.location);
		this.country = createElement('span', {className: 'text metro'});
		this.location.appendChild(this.country);
		this.state = createElement('span', {className: 'text metro'});
		this.location.appendChild(this.state);
		this.city = createElement('span', {className: 'text metro'});
		this.location.appendChild(this.city);
		this.join = createElement('div', {className: 'profile-follow text metro', innerText: 'follow', css: {display: 'none'}});
		this.details.appendChild(this.join);
		this.left.appendChild(this.clubAboutContainer);

		this.isJoined = false;
		this.joinRequest = null;
		this.join.on('click', () => {
			const change = {club_id: this.id};

			if(this.isJoined){
				change.toJoin = false;

				this.join.setText('Join');
				this.isJoined = false;
			}else{
				change.toJoin = true;

				this.join.setText('Leave');
				this.isJoined = true;
			}

			if(this.joinRequest){
				this.joinRequest.abort();
				this.joinRequest = null;

				return;
			}

			const x = this.joinRequest = accountManager.sendRequest('/join_leave_club', change, (status, error, resp) => {
				var err = false;

				if(error || status != 200){
					this.joinFailed.show();
					this.joinFailed.hideAfter(2000);

					err = true;
				}

				if(this.joinRequest == x){
					this.joinRequest = null;

					if(!err)
						return;
					if(this.isJoined){
						this.join.setText('Join');
						this.isJoined = false;
					}else{
						this.join.setText('Leave');
						this.isJoined = true;
					}
				}
			});
		});

		this.members = createElement('div', {className: 'profile-clubs shadow-light'});
		this.membersTitleContainer = createElement('div', {className: 'profile-clubs-title-container', css: {'flex-direction': 'row', 'justify-content': 'center'}});
		this.membersTitle = createElement('span', {className: 'profile-clubs-title text metro', innerText: 'Members'});
		this.membersTitleContainer.appendChild(this.membersTitle);
		this.membersCount = createElement('span', {className: 'club-members-count text'});
		this.membersTitleContainer.appendChild(this.membersCount);
		this.members.appendChild(this.membersTitleContainer);
		this.membersList = createElement('div', {className: 'profile-clubs-list'});
		this.members.appendChild(this.membersList);
		this.right.appendChild(this.members);

		this.fetch_activities = null;
		this.activity_last = null;
		this.activity_next = false;

		this.svg = createElement('svg', {className: 'activities-loader'});

		generateGradient(this.svg, {gradient: 'activities-gradient', mask: 'activities-mask'}, [
			{
				offset: "0%",
				color: "#0f0"
			},
			{
				offset: "100%",
				color: "#00f"
			}
		], [
			createElement('circle', {attributes: {cx: 50, cy: 50, r: 16}})
		]);

		this.hasCenterLoading = false;
		this.centerLoadingIndicator = createElement('div', {className: 'center'});
		this.hasLoading = false;
		this.loadingIndicator = createElement('div', {className: 'activities-loader-center'});

		this.element.on('scroll', (e) => {
			const min = Math.max(0, this.element.offsetHeight - 250);
			const pscroll = Math.max(0, this.element.scrollTop - Math.max(0,
				this.clubAboutContainer.offsetHeight - min));
			const cscroll = Math.max(0, this.element.scrollTop - Math.max(0,
				this.members.offsetHeight - min));
			this.clubAboutContainer.style.transform = 'translateY(' + pscroll + 'px)';
			this.members.style.transform = 'translateY(' + cscroll + 'px)';

			if(this.activity_next && !this.fetch_activities)
				if(this.element.offsetHeight + this.element.scrollTop + 800 >= this.middle.offsetHeight)
					this.fetchActivities(this.id, 0, this.activity_last);
		});
	}

	updateSize(){
		setTimeout(() => {
			this.clubAboutContainer.style.height = 120 + this.details.offsetHeight + 'px';
		}, 0);
	}

	load(data){
		if(!data)
			return pageManager.showErrorPage();
		document.title = data.name;
		history.replaceState(null, document.title, '/club/' + data.id);

		setBackgroundImage(this.clubImage, cdnPath('club', data.id));

		this.joinRequest = null;
		this.name.setText(data.name);
		this.type.setText(data.type);
		this.description.setText(data.description);
		this.country.setText(data.country);
		this.state.setText(data.state);
		this.city.setText(data.city);
		this.owner = data.owner;

		if(this.owner)
			this.join.style.display = 'none';
		else{
			this.join.style.display = '';
			this.isJoined = data.joined;

			if(this.isJoined)
				this.join.setText('Leave');
			else
				this.join.setText('Join');
		}

		this.updateSize();

		while(this.membersList.childNodes.length)
			this.membersList.removeChild(this.membersList.childNodes[0]);
		for(var i = 0; i < data.members.length; i++){
			const member = data.members[i];
			const container = createElement('a', {className: 'profile-clubs-photo-container', attributes: {href: '/profile/' + member.id}});
			const img = createElement('div', {className: 'profile-clubs-photo'});

			setBackgroundImage(img, cdnPath('profile', member.id));
			ajaxify(container);

			container.appendChild(img);

			this.membersList.appendChild(container);
		}

		this.membersCount.setText(data.members.length + '');

		if(this.fetch_activities)
			this.fetch_activities.abort();
		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(this.hasCenterLoading){
			this.centerLoadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.centerLoadingIndicator);
			this.hasCenterLoading = false;
		}

		while(this.middle.childNodes.length)
			this.middle.removeChild(this.middle.childNodes[0]);
		this.activity_last = null;
		this.activity_next = false;
		this.id = data.id;
		this.fetchActivities(data.id);

		if(!this.hasCenterLoading){
			this.centerLoadingIndicator.appendChild(this.svg);
			this.middle.appendChild(this.centerLoadingIndicator);
			this.hasCenterLoading = true;
		}
	}

	showActivities(list){
		if(this.hasCenterLoading){
			this.middle.removeChild(this.centerLoadingIndicator);
			this.middle.appendChild(createElement('div', {className: 'profile-container top-padding'}));
			this.hasCenterLoading = false;
		}

		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(!list){
			this.middle.appendChild(createElement('span', {className: 'profile-activity-error text', innerText: 'Could not load activities at this moment'}));

			return;
		}else{
			for(var i = 0; i < list.length; i++){
				this.activity_last = list[i].id;
				this.middle.appendChild(activityManager.createActivity(list[i]));
			}

			if(list.length){
				this.activity_next = true;

				if(!this.hasLoading){
					this.loadingIndicator.appendChild(this.svg);
					this.middle.appendChild(this.loadingIndicator);
					this.hasLoading = true;
				}
			}else if(!this.activity_last)
				this.middle.appendChild(createElement('span', {className: 'profile-activity-error text', innerText: 'This club has no activities'}));
		}
	}

	fetchActivities(id, top = 50, last){
		this.fetch_activities = accountManager.sendRequest('/activities?clubId=' + id + '&top=' + top + (last ? '&last=' + last : ''), null, (status, error, resp) => {
			this.activity_next = false;
			this.fetch_activities = null;

			if(error || status != 200)
				this.showActivities(null);
			else
				this.showActivities(resp.activities);
		});
	}

	hidden(){
		if(this.fetch_activities){
			this.fetch_activities.abort();
			this.fetch_activities = null;
		}
	}
}

class ExplorePage extends Page{
	load(data){
		/* todo */

		document.title = 'Explore';
		history.replaceState(null, document.title, '/explore');
	}
}

class DashboardPage extends Page{
	load(data){
		/* todo */

		document.title = 'Account';
		history.replaceState(null, document.title, '/dashboard');
	}
}

function generateGradient(svg, ids, stops, maskch = []){
	const gradient = createElement('linearGradient', {id: ids.gradient, attributes: {'x1': '0%', 'y1': '0%', 'x2': '100%', 'y2': '100%'}});

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
			feed: new FeedPage(),
			explore: new ExplorePage(),
			dashboard: new DashboardPage(),
			notfound: new NotFoundPage(),
			new_activity: new ActivityCreationPage(),
			activity: new ActivityPage(),
			new_club: new ClubCreationPage(),
			club: new ClubPage(),
			unverified: new UnverifiedPage()
		};

		this.showingPage = null;
		this.body = createElement('div', {className: 'page-container'});
		this.topBar = new (class{
			constructor(){
				this.element = createElement('div', {className: 'top-bar shadow-light'});
				this.logo = createElement('svg', {className: 'logo-container', css: {cursor: 'pointer'}});
				this.logoWaves = createElement('div', {className: 'logo'}, [this.logo]);
				this.element.appendChild(this.logoWaves);
				this.logo.on('click', (e) => {
					e.preventDefault();
					pageLoader.load('/');

					return false;
				});

				this.element.appendChild(this.createItem('Dashboard', '/dashboard'));
				this.element.appendChild(this.createItem('Explore', '/explore'));
				this.element.appendChild(this.createItem('Challenges', '/challenges'));

				ripplingManager.initElement(this.logoWaves, 'rgba(0, 0, 0, 0.05)');

				generateGradient(this.logo, {gradient: 'logo-gradient', mask: 'logo-mask'}, [
					{
						offset: '0%',
						color: '#f00'
					},
					{
						offset: '100%',
						color: '#00f'
					}
				], [
					createElement('path', {attributes: {'d': 'M 0,0 15,40 25,40 40,0 30,0 20,28 10,0 0,0z M 35,40 50,0 60,0 75,40 65,40 55,12 45,40 z M 80,0 80,40 90,40 90,25 95,25 A12.5,12.5 0 1,0 95,0 M 95,0 80,0z M 113,0 113,40 143,40 143,30 123,30 123,0 113,0z M 148,0 148,40 178,40 178,30 158,30 158,25 178,25 178,15 158,15 158,10 178,10 178,0 148,0 z'}})
				]);

				this.right = createElement('div', {className: 'right'});
				this.element.appendChild(this.right);
				this.login = this.createItem('Login', '/login');
				this.logout = createElement('a', {className: 'quick-nav text metro', innerText: 'Logout'});
				this.logout.on('click', () => {
					accountManager.logout();
					location = location.href;
				});

				this.logoutShowing = false;
				this.loginShowing = false;
				this.profileImage = createElement('div', {className: 'top-bar-profile-photo'});
				this.right.appendChild(this.profileImage);
				this.accountOptions = createElement('div', {className: 'top-bar-profile-options shadow-heavy', css: {display: 'none'}, attributes: {tabindex: 0}});
				this.right.appendChild(this.accountOptions);
				this.showProfilePhoto(cdnPath(null, 'default'));
				this.login.style.display = 'none';
				this.logout.style.display = 'none';
				this.accountOptions.appendChild(this.login);
				this.accountOptions.appendChild(this.logout);
				this.myProfile = this.createItem('My Profile', '')
				this.myProfile.style.display = 'none';
				this.accountOptions.appendChild(this.myProfile);

				this.profileImage.on('click', () => {
					this.accountOptions.style.display = '';
					this.accountOptions.focus();
				});

				this.accountOptions.on('blur', () => {
					this.accountOptions.style.display = 'none';
				});

				this.accountOptions.on('mousedown', (e) => {
					e.preventDefault();

					return false;
				});

				this.accountOptions.on('click', (e) => {
					this.accountOptions.blur();
				});

				this.newActivityButton = createElement('a', {className: 'top-bar-new-activity-container', attributes: {href: '/new_activity'}});
				this.svg = createElement('svg', {className: 'top-bar-new-activity', attributes: {viewBox: '0 0 48 48'}});

				ajaxify(this.newActivityButton);
				generateGradient(this.svg, {gradient: 'activity-add-gradient', mask: 'activity-add-mask'}, [
					{
						offset: "0%",
						color: "#f00"
					},
					{
						offset: "100%",
						color: "#00f"
					}
				], [
					createElement('path', {attributes: {d: 'M23.5,23.5 m -19.5 0 a 19.5,19.5 0 1,0 39,0 a 19.5,19.5 0 1,0 -39,0 z', stroke: '#fff', 'stroke-width': '3px'}}),
					createElement('path', {attributes: {d: 'M14,22 22,22 22,14 25,14 25,22 33,22 33,25 25,25 25,33 22,33 22,25 25,25 14,25z', fill: '#fff'}})
				]);

				this.newActivityButton.appendChild(this.svg);
				this.right.appendChild(this.newActivityButton);
			}

			createItem(text, path){
				const el = createElement('a', {className: 'quick-nav text metro', innerText: text, attributes: {href: path}});

				ajaxify(el);

				return el;
			}

			showProfilePhoto(url){
				setBackgroundImage(this.profileImage, url);
			}

			showLogin(show){
				if(show != this.loginShowing){
					this.loginShowing = show;

					if(show)
						this.login.style.display = '';
					else
						this.login.style.display = 'none';
				}
			}

			showLogout(show){
				if(show != this.logoutShowing){
					this.logoutShowing = show;

					if(show)
						this.logout.style.display = '';
					else
						this.logout.style.display = 'none';
				}
			}

			showMyProfile(id){
				this.myProfile.setAttribute('href', '/profile/' + id);
				this.myProfile.style.display = '';
			}
		});

		this.body.appendChild(this.topBar.element);

		document.body.appendChild(this.body);
	}

	showPage(page){
		if(this.showingPage != page){
			if(this.showingPage){
				this.body.removeChild(this.showingPage.element);
				this.showingPage.hidden();
			}

			this.showingPage = page;
			this.body.appendChild(page.element);

			page.showing();
		}
	}

	showErrorPage(){
		this.showPage(this.pages.error);
	}

	showNotFoundPage(){
		this.showPage(this.pages.notfound);
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

const pageLoader = new (class{
	constructor(){
		this.loading = null;
		this.loadingToast = new LoadingToast('Navigating page');
		this.loadingToast.indeterminate();
		this.waitforlogin = new Toast('Please wait for login/signup');

		toastManager.addToast(this.loadingToast);
		toastManager.addToast(this.waitforlogin);

		window.addEventListener('popstate', (e) => {
			this.load(location.href, false);
		});
	}

	load(url, state = true){
		if(state)
			history.pushState(null, '', url);
		if(accountManager.hasAccount || !accountManager.needAccount){
			do{
				if(!url.startsWith('/')){
					var u;

					try{
						u = new URL(url);
						url = u.pathname;
					}catch(e){
						break;
					}
				}

				const spl = url.split(/[/]+/);

				if(spl[1] == 'login'){
					if(!accountManager.hasAccount){
						pageManager.load({type: 'login', data: null});

						this.stopLoading();
						this.loadingToast.hide();
					}

					return;
				}
			}while(false);
		}

		this.stopLoading();

		if(accountManager.loggingIn){
			this.waitforlogin.show();
			this.waitforlogin.hideAfter(5000);

			return;
		}

		const l = accountManager.sendRequest(url, null, (status, error, resp) => {
			this.loadingToast.hideAfter(200);

			if(resp)
				pageManager.load(resp);
			if(status == 404)
				pageManager.showNotFoundPage();
			else if(error)
				pageManager.showErrorPage();
			else if(status != 200)
				pageManager.showErrorPage();
		});

		this.loading = l;
		this.loadingToast.show();
	}

	stopLoading(){
		if(this.loading && this.loading.readyState < 4){
			this.loading.abort();
			this.loading = null;
			this.loadingToast.hideAfter(200);
		}
	}
});

const accountManager = new (class{
	constructor(){
		this.needAccount = false;
		this.hasAccount = false;
		this.loggingIn = false;
		this.submitting = false;
		this.token = localStorage.token;
		this.id = null;

		if(this.token)
			this.needAccount = true;
		this.updateButtons();
		this.expired = new Toast('Account credentials expired');

		toastManager.addToast(this.expired);
	}

	needLogin(){
		return !this.loggingIn && !this.token;
	}

	load(account){
		this.needAccount = false;

		if(account){
			if(account.expired){
				this.expired.show();
				this.expired.hideAfter(5000);
				this.token = null;

				this.updateButtons();

				delete localStorage.token;
				return true;
			}

			this.hasAccount = true;

			pageManager.topBar.showProfilePhoto(cdnPath('profile', account.id));
			pageManager.topBar.showMyProfile(account.id);

			this.id = account.id;

			return true;
		}

		return false;
	}

	complete(data){
		this.loggingIn = false;

		pageManager.pages.login.finish(data.token, data.error);

		if(data.error || !data.token)
			return false;
		if(data.account)
			return true;
		pageManager.showErrorPage();

		return false;
	}

	updateButtons(){
		if(this.token){
			pageManager.topBar.showLogout(true);
			pageManager.topBar.showLogin(false);
		}else{
			pageManager.topBar.showLogout(false);
			pageManager.topBar.showLogin(true);
		}
	}

	authenticate(path, content){
		if(!this.needLogin())
			return;
		this.loggingIn = true;

		pageLoader.stopLoading();

		this.sendRequest(path, content, (status, error, data) => {
			if(error || status != 200)
				return this.complete({});
			if(this.complete(data)){
				localStorage.token = data.token;

				this.load(data.account);
				this.token = data.token;
				this.updateButtons();

				pageLoader.load('/');
			}
		});
	}

	changeAccount(name, bio, country, state, city, image){
		this.submitting = true;

		const data = {name: name, location_country: country, location_state: state, location_city: city, description: bio};
		const x = new XMLHttpRequest();

		if(image)
			data.image = image;
		this.sendRequest('/account_change', data, (status, error, resp) => {
			this.submitting = false;

			if(error || status != 200)
				return pageManager.pages.profile.updateResult();
			pageManager.pages.profile.updateResult(resp);
		});
	}

	login(email, password){
		this.authenticate('/account_login', {email, password});
	}

	signup(name, email, password, country, state, city){
		this.authenticate('/account_create', {email, password, name, location_country: country, location_state: state, location_city: city});
	}

	logout(){
		this.token = null;

		delete localStorage.token;
	}

	sendRequest(url, data, callback){
		const x = new XMLHttpRequest();

		x.open('POST', url);

		if(this.token)
			x.setRequestHeader('Authentication', this.token);
		if(this.needAccount){
			if(!data)
				data = {};
			data.retrieveAccount = true;
		}

		if(data)
			x.send(JSON.stringify(data));
		else
			x.send();
		x.addEventListener('load', () => {
			var data;

			try{
				data = JSON.parse(x.response);
			}catch(e){
				return callback(x.status, new Error("invalid json response"), null);
			}

			if(accountManager.needAccount)
				if((x.status == 200 || data.account) && !accountManager.load(data.account))
					return callback(x.status, new Error("expected an account"), null);
			callback(x.status, null, data);
		});

		x.addEventListener('error', (e) => {
			callback(null, e, null);
		});

		return x;
	}
});

window.vaple = {pageManager, pageLoader, toastManager};