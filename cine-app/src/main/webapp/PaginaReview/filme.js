
const likeCounts = [];
const dislikeCounts = [];


const btnLikes = document.querySelectorAll('.btnLike');
const btnDislikes = document.querySelectorAll('.btnDislike');


btnLikes.forEach((btn, index) => {
  likeCounts[index] = 0;
});
btnDislikes.forEach((btn, index) => {
  dislikeCounts[index] = 0;
});


btnLikes.forEach((btn, index) => {
  btn.addEventListener('click', () => {
    likeCounts[index]++;
    const likeCountEl = btn.nextElementSibling; 
    likeCountEl.textContent = likeCounts[index];
    likeCountEl.style.display = 'inline'; 
  });
});


btnDislikes.forEach((btn, index) => {
  btn.addEventListener('click', () => {
    dislikeCounts[index]++;
    const dislikeCountEl = btn.nextElementSibling; 
    dislikeCountEl.textContent = dislikeCounts[index];
    dislikeCountEl.style.display = 'inline'; 
  });
});







