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
	element.style['background-image'] = 'url(' + url + ')';
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
		this.successText = createElement('span', {className: 'text metro', css: {'font-size': '12px', 'margin-top': '40px'}});
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
			if(this.followRequest)
				this.followRequest.abort();
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

			this.followRequest = accountManager.sendRequest('/follow_change', change, (status, error, resp) => {
				this.followRequest = null;

				if(error || status != 200){
					this.followFailed.show();
					this.followFailed.hideAfter(2000);
				}
			});
		});

		this.clubs = createElement('div', {className: 'profile-clubs shadow-light'});
		this.clubs.appendChild(createElement('span', {className: 'profile-clubs-title text metro', innerText: 'Clubs'}));
		this.noneText = createElement('span', {className: 'text metro', css: {display: 'none'}});
		this.clubs.appendChild(this.noneText);
		this.clubsList = createElement('div', {className: 'profile-clubs-list'});
		this.clubs.appendChild(this.clubsList);
		this.right.appendChild(this.clubs);

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

			container.appendChild(img);
			container.on('click', (e) => {
				e.preventDefault();
				pageLoader.load(container.getAttribute('href'));

				return false;
			})

			this.clubsList.appendChild(container);
		}

		this.noneText.style.display = club_count ? 'none' : '';

		if(this.fetch_activities)
			this.fetch_activities.abort();
		while(this.middle.childNodes.length)
			this.middle.removeChild(this.middle.childNodes[0]);
		this.activity_last = null;
		this.activity_next = false;
		this.id = data.id;
		this.fetchActivities(data.id);

		if(this.hasLoading){
			this.loadingIndicator.removeChild(this.svg);
			this.middle.removeChild(this.loadingIndicator);
			this.hasLoading = false;
		}

		if(!this.hasCenterLoading){
			this.centerLoadingIndicator.appendChild(this.svg);
			this.middle.appendChild(this.centerLoadingIndicator);
			this.hasCenterLoading = true;
		}
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

			creatorPhoto.on('click', function(e){
				e.preventDefault();
				pageLoader.load(this.getAttribute('href'));

				return false;
			});

			left.appendChild(creatorPhoto);
			left.appendChild(createElement('span', {className: 'text metro', innerText: data.creator.name}));

			if(data.club){
				const clubPhoto = createElement('a', {className: 'profile-activity-club-photo', attributes: {href: '/club/' + data.club.id}});

				setBackgroundImage(clubPhoto, cdnPath('club', data.club.id));

				clubPhoto.on('click', function(e){
					e.preventDefault();
					pageLoader.load(this.getAttribute('href'));

					return false;
				});

				right.appendChild(clubPhoto);
				right.appendChild(createElement('span', {className: 'text metro', innerText: data.club.name}));
			}

			container.appendChild(left);
			container.appendChild(right);
			body.appendChild(container);
		}while(false);

		do{
			const title = this.createOffsetContainer();

			title.appendChild(createElement('a', {className: 'profile-activity-title text metro', innerText: data.name, href: '/activity/' + data.id}));
			body.appendChild(title);

			title.on('click', function(e){
				e.preventDefault();
				pageLoader.load(this.getAttribute('href'));

				return false;
			});

			const time = this.createOffsetContainer();
			var now = Date.now();
			var text;

			if(now < data.time_start){
				const date = new Date(data.time_start);
				const month = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
				var hours = date.getHours();
				var am = hours < 12;

				if(hours > 12)
					hours -= 12;
				else if(hours == 0)
					hours = 12;
				text = month[date.getMonth()] + ' ' + date.getDate() + ' at ' + hours + ':';

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

			time.appendChild(createElement('div', {className: 'profile-activity-time text metro', innerText: text}));
			body.appendChild(time);

			const description = this.createOffsetContainer();

			description.appendChild(createElement('span', {className: 'profile-activity-description text', innerText: data.description}));
			body.appendChild(description);
		}while(false);

		do{
			const container = this.createOffsetContainer();

			container.appendChild(createElement('iframe', {className: 'profile-activity-location shadow-light', attributes:
				{src: 'https://maps.google.com/maps?q=' + data.latitude + ',' + data.longitude + '&z=18&output=embed'}
			}));

			body.appendChild(container);
		}while(false);

		do{
			const text = this.createOffsetContainer();

			text.appendChild(createElement('span', {className: 'text metro', innerText: 'People Attending'}));
			body.appendChild(text);

			const attending = this.createOffsetContainer();

			for(var i = 0; i < data.attending.length; i++){
				const el = createElement('a', {className: 'profile-activity-attending-photo', attributes: {href: '/profile/' + data.attending[i].id}});

				el.on('click', function(e){
					e.preventDefault();
					pageLoader.load(this.getAttribute('href'));

					return false;
				});

				setBackgroundImage(el, cdnPath('profile', data.attending[i].id));

				attending.appendChild(el);
			}

			body.appendChild(attending);
		}while(false);

		return body;
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
				this.middle.appendChild(this.createActivity(list[i]));
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
		this.fetch_activities = accountManager.sendRequest('/activities?id=' + id + '&top=' + top + '&last=' + last, null, (status, error, resp) => {
			this.activity_next = false;
			this.fetch_activities = null;

			if(error || status != 200)
				this.showActivities(null);
			else
				this.showActivities(resp);
		});
	}

	hidden(){
		if(this.fetch_activities){
			this.fetch_activities.abort();
			this.fetch_activities = null;
		}
	}
}

class FeedPage extends Page{
	load(data){
		/* todo */

		document.title = 'Your feed';
		history.replaceState(null, document.title, '/');
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
			notfound: new NotFoundPage()
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
			}

			createItem(text, path){
				const el = createElement('a', {className: 'quick-nav text metro', innerText: text, attributes: {href: path}});

				el.on('click', (e) => {
					e.preventDefault();
					pageLoader.load(el.getAttribute('href'));

					return false;
				});

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

			if(error)
				return pageManager.showErrorPage();
			if(status == 404)
				pageManager.showNotFoundPage();
			else if(status != 200)
				pageManager.showErrorPage();
			else
				pageManager.load(resp);
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

			if(accountManager.needAccount && !accountManager.load(data.account))
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