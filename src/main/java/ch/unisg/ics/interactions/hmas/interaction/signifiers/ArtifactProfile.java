package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.Artifact;
import ch.unisg.ics.interactions.hmas.core.hostables.ResourceProfile;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class ArtifactProfile extends ResourceProfile {

  private final Set<Signifier> signifiers;

  protected ArtifactProfile(AbstractBuilder builder) {
    super(builder);
    this.signifiers = ImmutableSet.copyOf(builder.signifiers);
  }

  public Set<Signifier> getExposedSignifiers() {
    return this.signifiers;
  }

  public Artifact getArtifact() {
    return (Artifact) this.getResource();
  }

  public static class Builder extends AbstractBuilder<Builder, ArtifactProfile> {

    public Builder(Artifact artifact) {
      super(artifact);
    }

    public ArtifactProfile build() {
      return new ArtifactProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends ResourceProfile>
          extends ResourceProfile.AbstractBuilder<S, T> {

    private final Set<Signifier> signifiers;

    public AbstractBuilder(Artifact artifact) {
      super(artifact);
      this.signifiers = new HashSet<>();
    }

    public S exposeSignifier(Signifier signifier) {
      this.signifiers.add(signifier);
      return (S) this;
    }

    public S exposeSignifiers(Set<Signifier> signifiers) {
      this.signifiers.addAll(signifiers);
      return (S) this;
    }

    public abstract T build();
  }
}
