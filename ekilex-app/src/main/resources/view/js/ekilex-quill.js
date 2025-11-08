$(function () {
  const Italic = Quill.import("formats/italic");
  Italic.tagName = "i";
  Quill.register(Italic, true);
});

$.fn.initQuill = function () {
  return this.each(function () {
    const obj = $(this);
    obj.on("show.bs.modal", function () {
      const editor = new Quill("#quill", {
        theme: "snow",
        modules: {
          toolbar: {
            container: "#toolbar",
          },
        },
      });
    });
  });
};
